package cn.ianzb.miuixguitemplate.xposed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.ianzb.miuixguitemplate.hook.status.HookStatusContract
import cn.ianzb.miuixguitemplate.prefs.OptionRegistry

/**
 * 接收目标进程的 Hook 生效回报（App 侧）。
 *
 * 安全校验（防止任意应用伪造「已生效」证据）：
 * 1. 动作匹配 + 发送者身份可用（Android 16+ 需发送方 `setShareIdentityEnabled(true)`）；
 * 2. `getPackagesForUid(senderUid)` 必须包含其声称的来源包名；
 * 3. 模块 versionCode 必须与当前一致（0 视为未知、放行）；
 * 4. 上报的键必须是 App 已声明的配置键子集。
 *
 * 仅做上述校验、不申请系统签名级权限，普通模块即可在非系统签名下使用。
 */
class HookStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != HookStatusContract.ACTION_HOOKS_ACTIVE) return

        val senderUid = runCatching { sentFromUid }.getOrDefault(-1)
        if (senderUid < 0) {
            Log.w(TAG, "rejected hook report: sender identity unavailable")
            return
        }
        val senderPackages = context.packageManager
            .getPackagesForUid(senderUid)
            .orEmpty()
            .toList()

        val sourcePackage = intent.getStringExtra(HookStatusContract.EXTRA_SOURCE_PACKAGE)
        if (sourcePackage == null || sourcePackage !in senderPackages) {
            Log.w(TAG, "rejected hook report: source package mismatch uid=$senderUid")
            return
        }

        val reportedVersion = intent.getLongExtra(HookStatusContract.EXTRA_MODULE_VERSION, 0L)
        HookStatusStore.initialize(context)
        val currentVersion = HookStatusStore.currentVersionCode()
        if (reportedVersion != 0L && currentVersion != 0L && reportedVersion != currentVersion) {
            Log.w(TAG, "rejected hook report: module version mismatch ($reportedVersion != $currentVersion)")
            return
        }

        val knownKeys = OptionRegistry.all().mapTo(HashSet()) { it.key }
        val applied = intent
            .getStringArrayListExtra(HookStatusContract.EXTRA_INSTALLED_KEYS)
            .orEmpty()
            .filter { it in knownKeys }
        if (applied.isEmpty()) {
            Log.w(TAG, "rejected hook report: no known keys from $sourcePackage")
            return
        }

        HookStatusStore.record(context, applied)
        Log.i(TAG, "recorded ${applied.size} applied hook keys from $sourcePackage")
    }

    private companion object {
        const val TAG = "HookStatus"
    }
}

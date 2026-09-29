package cn.ianzb.miuixguitemplate.hook.status

import android.annotation.SuppressLint
import android.app.BroadcastOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import cn.ianzb.miuixguitemplate.hook.xposed.HookHelper

/**
 * Hook 生效状态上报器（hook 进程侧）。
 *
 * 目标进程在完成一组 hook 注册后调用 [markInstalled] 记录已安装的配置键，并在
 * `onPackageReady` 结束时调用 [flush] 合并上报一次。
 *
 * 设计要点（相对「hook 侧写远程偏好」的优化）：
 * - 通过 `ActivityThread.currentApplication()` 反射取目标进程 Application 作为发送上下文，
 *   不额外 hook `Application.attach` 等私有生命周期边界，失败面更小；
 * - 同进程内合并为**一次**广播，附带本进程已安装键集合；
 * - Android 16+ 开启 `BroadcastOptions.setShareIdentityEnabled(true)`，使 App 侧可校验发送者 UID。
 */
object HookStatusReporter {

    private val installedKeys = LinkedHashSet<String>()

    /** 记录一个已成功安装的配置键。 */
    @Synchronized
    fun markInstalled(key: String) {
        if (key.isNotBlank()) installedKeys.add(key)
    }

    /**
     * 合并上报当前进程已安装的配置键。
     *
     * 无已安装键、或取不到进程上下文时静默跳过（不影响业务 hook）。
     */
    @Synchronized
    fun flush(sourcePackage: String, sourceProcess: String) {
        if (installedKeys.isEmpty()) return
        val context = currentApplication() ?: run {
            HookHelper.log("hook status: application context unavailable, skip report")
            return
        }
        val keys = ArrayList(installedKeys)
        report(context, sourcePackage, sourceProcess, keys)
    }

    @Synchronized
    fun reset() {
        installedKeys.clear()
    }

    private fun report(
        context: Context,
        sourcePackage: String,
        sourceProcess: String,
        keys: ArrayList<String>,
    ) {
        try {
            val intent = Intent(HookStatusContract.ACTION_HOOKS_ACTIVE)
                .setComponent(
                    ComponentName(
                        HookStatusContract.MODULE_PACKAGE,
                        HookStatusContract.RECEIVER_CLASS,
                    )
                )
                .putExtra(HookStatusContract.EXTRA_SOURCE_PACKAGE, sourcePackage)
                .putExtra(HookStatusContract.EXTRA_SOURCE_PROCESS, sourceProcess)
                .putExtra(HookStatusContract.EXTRA_MODULE_VERSION, moduleVersionCode(context))
                .putStringArrayListExtra(HookStatusContract.EXTRA_INSTALLED_KEYS, keys)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES or Intent.FLAG_RECEIVER_FOREGROUND)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                val options = BroadcastOptions.makeBasic().apply {
                    setShareIdentityEnabled(true)
                }
                context.sendBroadcast(intent, null, options.toBundle())
            } else {
                context.sendBroadcast(intent)
            }
            HookHelper.log("hook status reported: keys=$keys @ $sourceProcess")
        } catch (t: Throwable) {
            HookHelper.log("hook status report failed @ $sourceProcess", t)
        }
    }

    @SuppressLint("PrivateApi")
    private fun currentApplication(): Context? = runCatching {
        Class.forName("android.app.ActivityThread")
            .getMethod("currentApplication")
            .invoke(null) as? Context
    }.getOrNull()

    private fun moduleVersionCode(context: Context): Long = runCatching {
        context.packageManager
            .getPackageInfo(HookStatusContract.MODULE_PACKAGE, 0)
            .longVersionCode
    }.getOrDefault(0L)
}

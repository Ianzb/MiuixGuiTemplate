package cn.ianzb.miuixguitemplate.xposed

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.ianzb.miuixguitemplate.prefs.PrefsStore
import io.github.libxposed.service.HookedTarget
import io.github.libxposed.service.HotReloadResult
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

/**
 * LSPosed 服务绑定：作用域查询 / 主动申请、热重载触发、运行中目标查询。
 */
object XposedServiceManager {

    private var service: XposedService? = null

    var isActivated by mutableStateOf(false)
        private set

    var scope by mutableStateOf<List<String>>(emptyList())
        private set

    private val listener = object : XposedServiceHelper.OnServiceListener {
        override fun onServiceBind(service: XposedService) {
            this@XposedServiceManager.service = service
            isActivated = true
            PrefsStore.attachRemote(service.getRemotePreferences(PrefsStore.REMOTE_GROUP))
            refreshScope()
        }

        override fun onServiceDied(service: XposedService) {
            this@XposedServiceManager.service = null
            isActivated = false
            scope = emptyList()
            PrefsStore.attachRemote(null)
        }
    }

    fun init() {
        XposedServiceHelper.registerListener(listener)
    }

    fun getService(): XposedService? = service

    fun refreshScope() {
        scope = service?.scope ?: emptyList()
    }

    fun isInScope(packageName: String): Boolean = scope.contains(packageName)

    /**
     * 主动申请作用域：仅对尚未授权的包发起请求。
     */
    fun ensureScope(packages: List<String>, onResult: ((Boolean, String?) -> Unit)? = null) {
        val current = service
        if (current == null) {
            onResult?.invoke(false, "service unavailable")
            return
        }
        val missing = packages.filter { it.isNotEmpty() && it !in scope }
        if (missing.isEmpty()) {
            onResult?.invoke(true, null)
            return
        }
        current.requestScope(missing, object : XposedService.OnScopeEventListener {
            override fun onScopeRequestApproved(approved: List<String>) {
                refreshScope()
                onResult?.invoke(true, null)
            }

            override fun onScopeRequestFailed(message: String) {
                onResult?.invoke(false, message)
            }
        })
    }

    fun removeScope(packages: List<String>) {
        service?.removeScope(packages)
        refreshScope()
    }

    /**
     * 触发目标进程热重载。
     */
    fun hotReload(packages: List<String>, onResult: ((String) -> Unit)? = null) {
        val current = service
        if (current == null) {
            onResult?.invoke("service unavailable")
            return
        }
        val targets = current.runningTargets.filter { target ->
            packages.any { pkg -> target.processName == pkg || target.processName.startsWith("$pkg:") }
        }
        if (targets.isEmpty()) {
            onResult?.invoke("no running target")
            return
        }
        targets.forEach { target ->
            current.hotReloadModule(target, Bundle(), object : XposedService.HotReloadCallback {
                override fun onHotReloadResult(target: HookedTarget, result: HotReloadResult) {
                    onResult?.invoke(result.status().name)
                }
            })
        }
    }

    fun runningTargets(): List<HookedTarget> = service?.runningTargets ?: emptyList()
}

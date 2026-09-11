package cn.ianzb.miuixguitemplate.hook.base

import cn.ianzb.miuixguitemplate.hook.dexkit.DexKitCacheManager
import cn.ianzb.miuixguitemplate.hook.xposed.HookHelper
import cn.ianzb.miuixguitemplate.hook.xposed.HookStatusWriter

/**
 * 按目标包组织的一组 hook。参考 HyperCeiler 的 BaseLoad。
 *
 * 子类在 [onPackageLoaded] 中通过 [initHook] 声明本包需要安装的 hook 及其开关。
 */
abstract class BaseLoad {

    /** 本 Load 负责的目标包名。 */
    abstract val targetPackages: List<String>

    private val pendingHooks = mutableListOf<Pair<BaseHook, Boolean>>()

    @Volatile
    private var currentTarget: PackageTarget? = null

    /** 目标包就绪时回调，子类在此注册 hook。 */
    protected open fun onPackageLoaded(target: PackageTarget) {}

    /** 注册一条 hook 及其启用状态（通常来自配置键）。 */
    protected fun initHook(hook: BaseHook, enabled: Boolean) {
        pendingHooks.add(hook to enabled)
    }

    fun onPackageReady(target: PackageTarget) {
        currentTarget = target
        HookStatusWriter.startProcess(target.processName)
        pendingHooks.clear()
        runCatching { onPackageLoaded(target) }
            .onFailure { HookHelper.log("onPackageLoaded failed: ${target.packageName}", it) }

        val needsDexKit = pendingHooks.any { it.second && it.first.useDexKit() }
        if (needsDexKit && !target.isSystemServer && target.applicationInfo != null) {
            runCatching { DexKitCacheManager.init(target, target.packageName) }
                .onFailure { HookHelper.log("DexKit init failed: ${target.packageName}", it) }
        }

        pendingHooks.forEach { (hook, enabled) ->
            if (enabled) install(hook)
        }

        if (needsDexKit) runCatching { DexKitCacheManager.releaseBridge() }
        HookStatusWriter.flush()
    }

    private fun install(hook: BaseHook) {
        try {
            if (hook.useDexKit()) {
                hook.dexKitInitInProgress = true
                val ok = try {
                    hook.initDexKit()
                } finally {
                    hook.dexKitInitInProgress = false
                }
                if (!ok) {
                    HookStatusWriter.record(hook.key, false)
                    HookHelper.log("${hook.tag} skipped: initDexKit returned false")
                    return
                }
            }
            hook.init()
            HookStatusWriter.record(hook.key, true)
            HookHelper.log("${hook.tag} hook success @ ${currentTarget?.packageName}")
        } catch (t: Throwable) {
            HookStatusWriter.record(hook.key, false)
            HookHelper.log("${hook.tag} hook failed @ ${currentTarget?.packageName}", t)
        }
    }
}

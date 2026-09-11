package cn.ianzb.miuixguitemplate.hook.base

import cn.ianzb.miuixguitemplate.hook.dexkit.DexKitCacheManager
import cn.ianzb.miuixguitemplate.hook.dexkit.IDexKit
import cn.ianzb.miuixguitemplate.hook.dexkit.IDexKitList
import cn.ianzb.miuixguitemplate.hook.xposed.HookHelper

/**
 * 单条 hook 规则基类。参考 HyperCeiler 的 BaseHook。
 *
 * 子类实现 [init]，在其中使用 [HookHelper] 完成挂载。
 * 若需要 DexKit，覆写 [useDexKit] 并在 [initDexKit] 中解析成员。
 */
abstract class BaseHook {

    /** 日志 TAG。 */
    open val tag: String get() = javaClass.simpleName

    /** 绑定的配置键；用于 hook 状态上报与 UI 展示。 */
    open val key: String get() = javaClass.simpleName

    /** 是否需要 DexKit 会话。 */
    open fun useDexKit(): Boolean = false

    /**
     * DexKit 初始化阶段：只在这里解析成员，不要在 hook 回调里首次触发 DexKit。
     * 返回 false 表示跳过该 hook。
     */
    open fun initDexKit(): Boolean = true

    /** 执行挂载。 */
    abstract fun init()

    internal var dexKitInitInProgress = false

    // ---------------- DexKit 辅助 ----------------

    protected fun <T> requiredMember(memberKey: String, finder: IDexKit): T {
        val value = optionalMemberOrNull<T>(memberKey, finder)
            ?: throw IllegalStateException("$tag: required DexKit member not found: $memberKey")
        return value
    }

    protected fun <T> requiredMemberList(memberKey: String, finder: IDexKitList): List<T> {
        val value = optionalMemberListOrNull<T>(memberKey, finder)
        if (value.isNullOrEmpty()) {
            throw IllegalStateException("$tag: required DexKit member list not found: $memberKey")
        }
        return value
    }

    protected fun <T> optionalMember(memberKey: String, finder: IDexKit): T? =
        runCatching { optionalMemberOrNull<T>(memberKey, finder) }
            .onFailure { HookHelper.log("$tag: optional DexKit member failed: $memberKey", it) }
            .getOrNull()

    protected fun <T> optionalMemberList(memberKey: String, finder: IDexKitList): List<T> =
        runCatching { optionalMemberListOrNull<T>(memberKey, finder) }
            .onFailure { HookHelper.log("$tag: optional DexKit member list failed: $memberKey", it) }
            .getOrNull()
            .orEmpty()

    @Suppress("UNCHECKED_CAST")
    private fun <T> optionalMemberOrNull(memberKey: String, finder: IDexKit): T? =
        DexKitCacheManager.findMember<T>(namespaced(memberKey), finder)

    @Suppress("UNCHECKED_CAST")
    private fun <T> optionalMemberListOrNull(memberKey: String, finder: IDexKitList): List<T>? =
        DexKitCacheManager.findMemberList<T>(namespaced(memberKey), finder)

    private fun namespaced(memberKey: String): String = "$tag#$memberKey"
}

package cn.ianzb.miuixguitemplate.hook.rule

/**
 * 版本分支：不同版本区间可以应用不同的 hook 代码。
 *
 * ```kotlin
 * override val variants = listOf(
 *     hookVariant("new", { app("com.miui.home") { ge("8.01.02.7709") } }) {
 *         // 新版本桌面：按指令签名定位
 *     },
 *     hookVariant("legacy", { app("com.miui.home") { lt("8.01.02.7709") } }) {
 *         // 旧版本桌面：按类名定位
 *     },
 * )
 * ```
 *
 * 分支按声明顺序匹配，命中第一个满足 [gate] 的分支；若 [BaseHook.variants] 非空但全部不匹配，
 * 则抛出 [HookSkippedException]（不安装、不记录状态）。
 */
class HookVariant(
    val name: String,
    val gate: HookVersionGate,
    val body: () -> Unit,
) {
    internal fun matches(context: VersionContext): Boolean = gate.matches(context)

    override fun toString(): String = "$name($gate)"
}

/** 构建一个版本分支。 */
fun hookVariant(
    name: String,
    mode: MatchMode = MatchMode.ALL,
    gate: HookVersionGateBuilder.() -> Unit,
    body: () -> Unit,
): HookVariant = HookVariant(name, hookVersionGate(mode, gate), body)

/**
 * 版本筛选未命中时抛出，用于把「主动跳过」与「安装失败」区分开：
 * 主动跳过不会写入失败状态（UI 显示为未应用）。
 */
class HookSkippedException(message: String) : RuntimeException(message)

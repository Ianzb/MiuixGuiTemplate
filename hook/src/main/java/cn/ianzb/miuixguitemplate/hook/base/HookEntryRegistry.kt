package cn.ianzb.miuixguitemplate.hook.base

import cn.ianzb.miuixguitemplate.hook.demo.DemoLoad

/**
 * 目标 Load 注册表。
 *
 * 新增目标包时，在这里登记对应的 [BaseLoad] 实现即可。
 */
object HookEntryRegistry {

    private val loads: List<BaseLoad> = listOf(
        DemoLoad(),
    )

    fun loadsFor(packageName: String): List<BaseLoad> =
        loads.filter { it.targetPackages.contains(packageName) }

    fun all(): List<BaseLoad> = loads
}

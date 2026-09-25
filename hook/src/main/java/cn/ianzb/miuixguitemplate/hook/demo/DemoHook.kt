package cn.ianzb.miuixguitemplate.hook.demo

import cn.ianzb.miuixguitemplate.hook.base.BaseHook
import cn.ianzb.miuixguitemplate.hook.base.BaseLoad
import cn.ianzb.miuixguitemplate.hook.base.PackageTarget
import cn.ianzb.miuixguitemplate.hook.prefs.HookPrefs
import cn.ianzb.miuixguitemplate.hook.xposed.HookHelper
import cn.ianzb.miuixguitemplate.hook.xposed.Reflect

/**
 * 最小可运行示例 hook（不参与示例页）。
 *
 * 目标：com.android.settings
 * 开关：demo_settings_hook（默认关闭）
 * 行为：仅记录 Activity.onCreate，不改变任何业务逻辑，用于验证整套封装：
 *   - 挂载 / 卸载
 *   - 配置读取
 *   - 状态上报
 */
class DemoLoad : BaseLoad() {

    override val targetPackages: List<String> = listOf(TARGET_PACKAGE)

    override fun onPackageLoaded(target: PackageTarget) {
        initHook(DemoHook(), HookPrefs.getBoolean(DemoHook.KEY, false))
    }

    companion object {
        const val TARGET_PACKAGE = "com.android.settings"
    }
}

class DemoHook : BaseHook() {

    override val key: String = KEY

    override fun init() {
        val activity = Reflect.findClass("android.app.Activity")
        HookHelper.findAndHookMethodAfter(
            activity,
            "onCreate",
            android.os.Bundle::class.java,
        ) { param ->
            HookHelper.log("DemoHook: onCreate -> ${param.thisObject?.javaClass?.name}")
        }
    }

    companion object {
        const val KEY = "demo_settings_hook"
    }
}

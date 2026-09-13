package cn.ianzb.miuixguitemplate.xposed

import android.content.Context
import android.content.Intent

/**
 * 应用重启 / 系统重启工具。
 *
 * - 普通应用：Root 下 `am force-stop` 后拉起启动 Activity。
 * - system_server（`system` / `android`）：只能通过重启系统恢复，执行 `reboot`。
 */
object AppRestarter {

    private val SYSTEM_PACKAGES = setOf("system", "android", "system_server")

    fun isSystemPackage(packageName: String): Boolean = packageName in SYSTEM_PACKAGES

    /**
     * 重启指定应用。
     *
     * @return true 表示操作已提交；false 表示缺少 Root 权限。
     */
    fun restart(context: Context, packageName: String): Boolean {
        if (!XposedServiceManager.isRootAvailable) return false
        if (isSystemPackage(packageName)) {
            return RootHelper.exec("reboot")
        }
        val stopped = RootHelper.exec("am force-stop $packageName")
        val launch = runCatching {
            context.packageManager.getLaunchIntentForPackage(packageName)
        }.getOrNull()
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { context.startActivity(launch) }
        }
        return stopped
    }

    /** 重启系统（用于 system_server 目标）。 */
    fun reboot(): Boolean =
        XposedServiceManager.isRootAvailable && RootHelper.exec("reboot")
}

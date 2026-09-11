package cn.ianzb.miuixguitemplate.hook.base

import android.app.Application
import android.content.pm.ApplicationInfo
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam
import io.github.libxposed.api.XposedModuleInterface.SystemServerStartingParam

/**
 * 目标进程信息封装，屏蔽 libxposed 参数细节。
 */
class PackageTarget(
    val packageName: String,
    val processName: String,
    val applicationInfo: ApplicationInfo?,
    val classLoader: ClassLoader?,
    val isSystemServer: Boolean = false,
) {
    companion object {

        fun from(param: PackageReadyParam): PackageTarget = PackageTarget(
            packageName = param.packageName,
            processName = param.applicationInfo.processName ?: param.packageName,
            applicationInfo = param.applicationInfo,
            classLoader = param.classLoader,
        )

        fun fromSystemServer(param: SystemServerStartingParam): PackageTarget = PackageTarget(
            packageName = "android",
            processName = "system_server",
            applicationInfo = null,
            classLoader = param.classLoader,
            isSystemServer = true,
        )

        /**
         * 热重载后重建目标信息（新代码代次拿不到 PackageReadyParam）。
         */
        fun restored(
            packageName: String,
            processName: String,
            applicationInfo: ApplicationInfo?,
        ): PackageTarget = PackageTarget(
            packageName = packageName,
            processName = processName,
            applicationInfo = applicationInfo,
            classLoader = currentClassLoader(),
        )

        /**
         * 尽力获取当前进程的 Application ClassLoader。
         */
        fun currentClassLoader(): ClassLoader? = runCatching {
            val thread = Class.forName("android.app.ActivityThread")
            val application = thread.getMethod("currentApplication").invoke(null) as? Application
            application?.classLoader
        }.getOrNull() ?: Thread.currentThread().contextClassLoader
    }
}

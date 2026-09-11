package cn.ianzb.miuixguitemplate.ui.component.pref

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.ianzb.miuixguitemplate.prefs.ConfigState
import cn.ianzb.miuixguitemplate.prefs.OptionRegistry
import cn.ianzb.miuixguitemplate.prefs.OptionSpec
import cn.ianzb.miuixguitemplate.xposed.HookStatus
import cn.ianzb.miuixguitemplate.xposed.HookStatusReader
import cn.ianzb.miuixguitemplate.xposed.XposedServiceManager
import top.yukonga.miuix.kmp.basic.Icon

/** 解析依赖项：依赖项满足条件时组件启用。 */
@Composable
fun rememberDependencyEnabled(spec: OptionSpec): Boolean {
    val dependencyKey = spec.dependsOn ?: return true
    val dependencyDefault = OptionRegistry.find(dependencyKey)?.defaultBoolean ?: false
    val dependencyValue = ConfigState.bool(dependencyKey, dependencyDefault)
    return if (spec.dependsOnValue) dependencyValue else !dependencyValue
}

/**
 * 选项被启用时，自动为未授权的作用域目标发起申请。
 */
fun ensureScopeFor(spec: OptionSpec) {
    if (spec.targetPackages.isNotEmpty()) {
        XposedServiceManager.ensureScope(spec.targetPackages)
    }
}

/** 计算某个配置项当前的 hook 状态。 */
@Composable
fun rememberHookStatus(spec: OptionSpec): HookStatus {
    spec.demoStatus?.let { return it }
    val activated = XposedServiceManager.isActivated
    val scope = XposedServiceManager.scope
    val raw = HookStatusReader.statusOf(spec.statusId)
    if (!activated) return HookStatus.NOT_APPLIED
    if (spec.targetPackages.isNotEmpty() && spec.targetPackages.none { it in scope }) {
        return HookStatus.NOT_APPLIED
    }
    return when (raw) {
        true -> HookStatus.SUCCESS
        false -> HookStatus.FAILED
        null -> HookStatus.NOT_APPLIED
    }
}

/**
 * 标题左侧的状态图标：成功显示对号，失败显示叉号，未应用时不显示（返回 null）。
 */
@Composable
fun HookStatusStartAction(spec: OptionSpec): (@Composable () -> Unit)? {
    val status = rememberHookStatus(spec)
    if (status == HookStatus.NOT_APPLIED) return null
    val imageVector = if (status == HookStatus.SUCCESS) Icons.Rounded.Check else Icons.Rounded.Close
    val tint = if (status == HookStatus.SUCCESS) Color(0xFF36D167) else Color(0xFFDC3545)
    return {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

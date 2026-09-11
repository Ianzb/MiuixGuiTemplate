package cn.ianzb.miuixguitemplate.ui.screen.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.prefs.OptionRegistry
import cn.ianzb.miuixguitemplate.ui.screen.scope.ScopeListActivity
import cn.ianzb.miuixguitemplate.ui.util.BlurredBar
import cn.ianzb.miuixguitemplate.ui.util.isInDarkTheme
import cn.ianzb.miuixguitemplate.ui.util.pageScrollModifiers
import cn.ianzb.miuixguitemplate.ui.util.rememberBlurBackdrop
import cn.ianzb.miuixguitemplate.ui.util.shouldShowSplitPane
import cn.ianzb.miuixguitemplate.util.SystemVersionDetector
import cn.ianzb.miuixguitemplate.xposed.XposedServiceManager
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * 模板主页。所有数据均为占位符，接入业务时替换为真实数据即可。
 */
@Composable
fun HomePageView(
    isBlurEnabled: Boolean = true,
    refreshKey: Int = 0,
    extraBottomPadding: Dp = 0.dp,
    onOpenExamples: () -> Unit = {},
) {
    val scrollBehavior = MiuixScrollBehavior()
    val title = stringResource(R.string.tab_home)

    val backdrop = rememberBlurBackdrop()
    val blurActive = isBlurEnabled && backdrop != null
    val barColor = if (blurActive) Color.Transparent else MiuixTheme.colorScheme.surface

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive, scrollBehavior) {
                TopAppBar(
                    title = title,
                    color = barColor,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box(
            modifier = if (isBlurEnabled && backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pageScrollModifiers(
                        showTopAppBar = true,
                        topAppBarScrollBehavior = scrollBehavior,
                    ),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + extraBottomPadding
                )
            ) {
                item {
                    val context = LocalContext.current
                    val isWideScreen = shouldShowSplitPane()
                    val darkTheme = isInDarkTheme()
                    val dynamicColor = MiuixTheme.isDynamicColor
                    val activated = XposedServiceManager.isActivated
                    val scopeCount = XposedServiceManager.scope.size
                    val featureCount = OptionRegistry.all().size

                    val statusColor = if (activated) {
                        when {
                            dynamicColor -> MiuixTheme.colorScheme.secondaryContainer
                            darkTheme -> Color(0xFF1A3825)
                            else -> Color(0xFFDFFAE4)
                        }
                    } else {
                        when {
                            dynamicColor -> MiuixTheme.colorScheme.errorContainer
                            darkTheme -> Color(0xFF3D1C1C)
                            else -> Color(0xFFFDE8E8)
                        }
                    }
                    val iconTint = if (activated) {
                        if (dynamicColor) MiuixTheme.colorScheme.primary.copy(alpha = 0.8f) else Color(0xFF36D167)
                    } else {
                        if (dynamicColor) MiuixTheme.colorScheme.error.copy(alpha = 0.8f) else Color(0xFFDC3545)
                    }

                    val openScopeList = {
                        context.startActivity(Intent(context, ScopeListActivity::class.java))
                    }

                    val cardsModifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                        .height(IntrinsicSize.Min)

                    if (isWideScreen) {
                        // 横屏：状态卡 / 作用域 / 功能总数 三卡片横向三等分。
                        Row(
                            modifier = cardsModifier,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusCard(
                                titleText = stringResource(R.string.module_status),
                                versionText = stringResource(
                                    if (activated) R.string.module_status_summary_activated
                                    else R.string.module_status_summary_inactive
                                ),
                                statusText = "",
                                success = activated,
                                iconTint = iconTint,
                                statusColor = statusColor,
                                onClick = {},
                                compact = true,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                            )
                            CountCard(
                                title = stringResource(R.string.home_scope),
                                count = scopeCount.toString(),
                                onClick = openScopeList,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                            )
                            CountCard(
                                title = stringResource(R.string.home_feature_count),
                                count = featureCount.toString(),
                                onClick = onOpenExamples,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                            )
                        }
                    } else {
                        Row(
                            modifier = cardsModifier,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusCard(
                                titleText = stringResource(R.string.module_status),
                                versionText = stringResource(
                                    if (activated) R.string.module_status_summary_activated
                                    else R.string.module_status_summary_inactive
                                ),
                                statusText = "",
                                success = activated,
                                iconTint = iconTint,
                                statusColor = statusColor,
                                onClick = {},
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                CountCard(
                                    title = stringResource(R.string.home_scope),
                                    count = scopeCount.toString(),
                                    onClick = openScopeList,
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                )
                                Spacer(Modifier.height(12.dp))
                                CountCard(
                                    title = stringResource(R.string.home_feature_count),
                                    count = featureCount.toString(),
                                    onClick = onOpenExamples,
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                )
                            }
                        }
                    }
                }

                item {
                    val context = LocalContext.current
                    val deviceModel = remember {
                        SystemVersionDetector.getMarketName().ifEmpty { "Unknown" }
                    }
                    val deviceName = remember {
                        try {
                            Settings.Global.getString(
                                context.contentResolver,
                                Settings.Global.DEVICE_NAME
                            )?.takeIf { it.isNotBlank() } ?: deviceModel
                        } catch (_: Exception) {
                            deviceModel
                        }
                    }
                    val hyperOsVersion = remember { SystemVersionDetector.getHyperOsVersion() }
                    val androidVersion = remember { SystemVersionDetector.getAndroidVersion() }
                    val appVersion = remember {
                        try {
                            context.packageManager
                                .getPackageInfo(context.packageName, 0).versionName ?: "1.0"
                        } catch (_: Exception) {
                            "1.0"
                        }
                    }

                    SmallTitle(
                        text = stringResource(R.string.home_device_info)
                    )
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)
                    ) {
                        Column {
                            BasicComponent(
                                title = stringResource(R.string.home_device_name),
                                summary = deviceName,
                            )
                            BasicComponent(
                                title = stringResource(R.string.home_device_model),
                                summary = deviceModel,
                            )
                            BasicComponent(
                                title = stringResource(R.string.home_hyperos_version),
                                summary = hyperOsVersion,
                            )
                            BasicComponent(
                                title = stringResource(R.string.home_android_version),
                                summary = androidVersion,
                            )
                            BasicComponent(
                                title = stringResource(R.string.home_app_version),
                                summary = appVersion
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    titleText: String,
    versionText: String,
    statusText: String,
    success: Boolean,
    iconTint: Color,
    statusColor: Color,
    onClick: () -> Unit,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val iconSize = if (compact) 100.dp else 170.dp
    val iconOffsetX = if (compact) 22.dp else 38.dp
    val iconOffsetY = if (compact) 26.dp else 45.dp
    Card(
        modifier = modifier,
        colors = CardDefaults.defaultColors(color = statusColor),
        onClick = onClick,
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(iconOffsetX, iconOffsetY),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = if (success) Icons.Rounded.CheckCircleOutline else Icons.Rounded.ErrorOutline,
                    tint = iconTint,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
            ) {
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = versionText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                MiuixText(
                    modifier = Modifier.fillMaxWidth(),
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CountCard(
    title: String,
    count: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(16.dp),
        showIndication = true,
        onClick = onClick,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            MiuixText(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            MiuixText(
                modifier = Modifier.fillMaxWidth(),
                text = count,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

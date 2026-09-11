package cn.ianzb.miuixguitemplate.ui.screen.scope

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.ui.screen.subpage.BaseSubPageActivity
import cn.ianzb.miuixguitemplate.xposed.XposedServiceManager
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/**
 * 作用域列表二级页面，实时反映当前已授权的作用域应用。
 *
 * 每项显示：应用图标 + 应用名称 + 包名 + 版本号。
 */
class ScopeListActivity : BaseSubPageActivity() {

    override val titleRes: Int = R.string.home_scope

    override fun onResume() {
        super.onResume()
        XposedServiceManager.refreshScope()
    }

    @Composable
    override fun SubPageContent(
        isBlurEnabled: Boolean,
        contentPadding: PaddingValues,
    ) {
        val context = LocalContext.current
        val density = LocalDensity.current
        val scope = XposedServiceManager.scope

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            if (scope.isEmpty()) {
                item {
                    MiuixText(
                        text = stringResource(R.string.scope_empty),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        style = MiuixTheme.textStyles.footnote2,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    )
                }
            } else {
                items(scope, key = { it }) { packageName ->
                    val packageManager = context.packageManager
                    val appInfo = remember(packageName) {
                        runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull()
                    }
                    val appName = remember(packageName, appInfo) {
                        appInfo?.let {
                            runCatching { packageManager.getApplicationLabel(it).toString() }.getOrNull()
                        } ?: packageName
                    }
                    val version = remember(packageName) {
                        runCatching { packageManager.getPackageInfo(packageName, 0).versionName }
                            .getOrNull().orEmpty()
                    }
                    val iconSizePx = with(density) { 40.dp.roundToPx() }
                    val icon = remember(packageName, appInfo, iconSizePx) {
                        appInfo?.let {
                            runCatching {
                                packageManager.getApplicationIcon(it)
                                    .toBitmap(iconSizePx, iconSizePx)
                                    .asImageBitmap()
                            }.getOrNull()
                        }
                    }

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(top = 12.dp)
                    ) {
                        BasicComponent(
                            title = appName,
                            summary = if (version.isNotBlank()) "$packageName · v$version" else packageName,
                            startAction = icon?.let { bitmap ->
                                {
                                    Icon(
                                        bitmap = bitmap,
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(40.dp),
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

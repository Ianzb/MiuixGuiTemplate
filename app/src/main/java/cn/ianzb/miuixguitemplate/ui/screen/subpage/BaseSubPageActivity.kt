package cn.ianzb.miuixguitemplate.ui.screen.subpage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cn.ianzb.miuixguitemplate.AppSettings
import cn.ianzb.miuixguitemplate.LocaleHelper
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.ui.component.SubPageScaffold
import cn.ianzb.miuixguitemplate.ui.theme.AppTheme
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

/**
 * 二级页面 Activity 模板。
 *
 * 自动套用模块配置（主题模式、背景模糊等），并带页面切换动画。
 * 子类只需提供标题与内容。
 */
abstract class BaseSubPageActivity : ComponentActivity() {

    @get:StringRes
    protected abstract val titleRes: Int

    @Composable
    protected abstract fun SubPageContent(
        isBlurEnabled: Boolean,
        contentPadding: PaddingValues,
    )

    override fun attachBaseContext(newBase: Context) {
        val language = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrapContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = AppSettings.load(this)
        val themeMode = try {
            ColorSchemeMode.valueOf(settings.themeMode)
        } catch (_: Exception) {
            ColorSchemeMode.System
        }

        setContent {
            AppTheme(themeMode = themeMode) {
                SubPageScaffold(
                    title = stringResource(titleRes),
                    isBlurEnabled = settings.isBlurEnabled,
                    onBack = { finish() },
                ) { padding ->
                    SubPageContent(settings.isBlurEnabled, padding)
                }
            }
        }
    }
}

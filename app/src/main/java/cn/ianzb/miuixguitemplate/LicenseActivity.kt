package cn.ianzb.miuixguitemplate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.ianzb.miuixguitemplate.ui.screen.about.LicensePageContent
import cn.ianzb.miuixguitemplate.ui.theme.AppTheme
import cn.ianzb.miuixguitemplate.ui.util.applyWindowBackground
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

class LicenseActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrapContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val savedSettings = AppSettings.load(this)
        val themeMode = try {
            ColorSchemeMode.valueOf(savedSettings.themeMode)
        } catch (_: Exception) {
            ColorSchemeMode.System
        }
        val isBlurEnabled = savedSettings.isBlurEnabled

        applyWindowBackground(savedSettings.themeMode)

        setContent {
            AppTheme(themeMode = themeMode) {
                LicensePageContent(onBack = { finish() }, isBlurEnabled = isBlurEnabled)
            }
        }
    }
}

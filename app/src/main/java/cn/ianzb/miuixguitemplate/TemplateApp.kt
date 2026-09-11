package cn.ianzb.miuixguitemplate

import android.app.Application
import cn.ianzb.miuixguitemplate.prefs.ConfigState
import cn.ianzb.miuixguitemplate.prefs.OptionRegistry
import cn.ianzb.miuixguitemplate.prefs.PrefsStore
import cn.ianzb.miuixguitemplate.ui.screen.examples.exampleSpecs
import cn.ianzb.miuixguitemplate.xposed.XposedServiceManager

class TemplateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PrefsStore.init(this)
        ConfigState.init(this)
        OptionRegistry.registerAll(exampleSpecs())
        XposedServiceManager.init()
    }
}

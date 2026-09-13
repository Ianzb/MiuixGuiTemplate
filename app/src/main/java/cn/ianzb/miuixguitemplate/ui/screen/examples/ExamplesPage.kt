package cn.ianzb.miuixguitemplate.ui.screen.examples

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.prefs.OptionSpec
import cn.ianzb.miuixguitemplate.prefs.OptionType
import cn.ianzb.miuixguitemplate.ui.component.pref.HookOptionsPage
import cn.ianzb.miuixguitemplate.ui.component.pref.HookSection
import cn.ianzb.miuixguitemplate.xposed.HookStatus
import cn.ianzb.miuixguitemplate.xposed.HookStatusReader

/**
 * 示例页：展示每个组件类型的实例（不含 hook 具体应用代码）。
 *
 * 页面布局由通用组件 [HookOptionsPage] 提供。
 */
@Composable
fun ExamplesPageView(
    isBlurEnabled: Boolean = true,
    extraBottomPadding: Dp = 0.dp,
) {
    val context = LocalContext.current
    val specs = remember { exampleSpecs() }
    val sections = remember(specs) { exampleSections(specs) }

    LaunchedEffect(Unit) {
        HookStatusReader.refresh()
    }

    HookOptionsPage(
        title = stringResource(R.string.tab_examples),
        sections = sections,
        isBlurEnabled = isBlurEnabled,
        extraBottomPadding = extraBottomPadding,
        onArrowClick = {
            context.startActivity(Intent(context, ExampleSubPageActivity::class.java))
        },
    )
}

private fun specByKey(specs: List<OptionSpec>, key: String): OptionSpec =
    specs.first { it.key == key }

private fun exampleSections(specs: List<OptionSpec>): List<HookSection> = listOf(
    HookSection(
        titleRes = R.string.example_section_switch,
        titleEn = "SwitchPreference",
        specs = listOf(specByKey(specs, "example_switch")),
    ),
    HookSection(
        titleRes = R.string.example_section_checkbox,
        titleEn = "CheckboxPreference",
        specs = listOf(specByKey(specs, "example_checkbox")),
    ),
    HookSection(
        titleRes = R.string.example_section_arrow,
        titleEn = "ArrowPreference",
        specs = listOf(specByKey(specs, "example_arrow")),
    ),
    HookSection(
        titleRes = R.string.example_section_dropdown,
        titleEn = "WindowDropdownPreference",
        specs = listOf(specByKey(specs, "example_dropdown")),
    ),
    HookSection(
        titleRes = R.string.example_section_radio,
        titleEn = "RadioButtonPreference",
        specs = listOf(specByKey(specs, "example_radio")),
    ),
    HookSection(
        titleRes = R.string.example_section_slider,
        titleEn = "SliderPreference",
        specs = listOf(specByKey(specs, "example_slider")),
    ),
    HookSection(
        titleRes = R.string.example_section_text,
        titleEn = "TextField",
        specs = listOf(specByKey(specs, "example_text")),
    ),
    HookSection(
        titleRes = R.string.example_section_package_list,
        titleEn = "PackageListPreference",
        specs = listOf(specByKey(specs, "example_package_list")),
    ),
    HookSection(
        titleRes = R.string.example_section_status,
        titleEn = "HookStatus",
        specs = listOf(
            specByKey(specs, "example_status_success"),
            specByKey(specs, "example_status_failed"),
            specByKey(specs, "example_status_none"),
        ),
    ),
)

/** 示例页的全部配置项（App 启动时注册，供全局搜索与作用域申请使用）。 */
internal fun exampleSpecs(): List<OptionSpec> = listOf(
    OptionSpec(
        key = "example_switch",
        type = OptionType.SWITCH,
        titleRes = R.string.example_switch_title,
        summaryRes = R.string.example_switch_summary,
        defaultBoolean = false,
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_checkbox",
        type = OptionType.CHECKBOX,
        titleRes = R.string.example_checkbox_title,
        summaryRes = R.string.example_checkbox_summary,
        defaultBoolean = false,
        dependsOn = "example_switch",
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_arrow",
        type = OptionType.ARROW,
        titleRes = R.string.example_arrow_title,
        summaryRes = R.string.example_arrow_summary,
    ),
    OptionSpec(
        key = "example_dropdown",
        type = OptionType.DROPDOWN,
        titleRes = R.string.example_dropdown_title,
        summaryRes = R.string.example_dropdown_summary,
        defaultString = "default",
        entryResIds = listOf(
            R.string.example_dropdown_default,
            R.string.example_dropdown_slow,
            R.string.example_dropdown_fast,
        ),
        entryValues = listOf("default", "slow", "fast"),
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_radio",
        type = OptionType.RADIO,
        titleRes = R.string.example_radio_title,
        summaryRes = R.string.example_radio_summary,
        defaultString = "home",
        entryResIds = listOf(
            R.string.example_radio_home,
            R.string.example_radio_discover,
            R.string.example_radio_mine,
        ),
        entryValues = listOf("home", "discover", "mine"),
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_slider",
        type = OptionType.SLIDER,
        titleRes = R.string.example_slider_title,
        summaryRes = R.string.example_slider_summary,
        defaultFloat = 100f,
        masterKey = "example_slider_enable",
        sliderMin = 50f,
        sliderMax = 200f,
        sliderStep = 1f,
        sliderDecimals = 0,
        sliderUnitRes = R.string.example_slider_unit,
        sliderValueLabelRes = R.string.example_slider_value_label,
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_slider_enable",
        type = OptionType.SWITCH,
        titleRes = R.string.example_slider_title,
        summaryRes = R.string.example_slider_summary,
        defaultBoolean = true,
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_text",
        type = OptionType.TEXT,
        titleRes = R.string.example_text_title,
        summaryRes = R.string.example_text_summary,
        defaultString = "",
        targetPackages = listOf("com.example.target"),
    ),
    OptionSpec(
        key = "example_package_list",
        type = OptionType.PACKAGE_LIST,
        titleRes = R.string.example_package_list_title,
        summaryRes = R.string.example_package_list_summary,
        defaultString = "",
    ),
    OptionSpec(
        key = "example_status_success",
        type = OptionType.SWITCH,
        titleRes = R.string.example_status_success_title,
        summaryRes = R.string.example_status_success_summary,
        defaultBoolean = true,
        demoStatus = HookStatus.SUCCESS,
    ),
    OptionSpec(
        key = "example_status_failed",
        type = OptionType.SWITCH,
        titleRes = R.string.example_status_failed_title,
        summaryRes = R.string.example_status_failed_summary,
        defaultBoolean = false,
        demoStatus = HookStatus.FAILED,
    ),
    OptionSpec(
        key = "example_status_none",
        type = OptionType.SWITCH,
        titleRes = R.string.example_status_none_title,
        summaryRes = R.string.example_status_none_summary,
        defaultBoolean = false,
        demoStatus = HookStatus.NOT_APPLIED,
    ),
)

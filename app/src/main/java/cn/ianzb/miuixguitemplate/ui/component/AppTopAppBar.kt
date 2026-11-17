package cn.ianzb.miuixguitemplate.ui.component

import android.os.Build
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.ui.util.BlurredBar
import cn.ianzb.miuixguitemplate.ui.util.LocalAppPageStartInset
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.BlurTopAppBar
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.glass.GlassIconButton
import top.yukonga.miuix.kmp.glass.GlassTopAppBar
import top.yukonga.miuix.kmp.glass.GlassTopAppBarDefaults
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.glass.ChevronBackward
import top.yukonga.miuix.kmp.material.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 页面顶栏：优先 HyperOS 4 玻璃顶栏（[GlassTopAppBar]），设备不支持运行时着色器时回退到
 * miuix 模糊顶栏（[BlurredBar] + [BlurTopAppBar]）。所有页面（含二级页）共用这一条路径。
 *
 * 参数取值参照 miuix 官方 demo（`utils/PageUtils.kt` 的 `AdaptiveTopAppBar`）：
 * 有 backdrop 时用透明着色让玻璃带采样页面内容，否则退回 [GlassTopAppBarDefaults.bandBrush]；
 * `defaultWindowInsetsPadding = false` 以免与各页 Scaffold 的内边距叠加。
 *
 * @param blurActive 顶栏模糊是否生效（通常为 `topBarBackdrop != null`）。
 * @param color 顶栏着色，模糊生效时各页传 `Color.Transparent`。
 * @param backdrop 玻璃采样源，由页面脚手架传入；为 null 时顶栏退化为渐变着色带。
 * @param onBack 非空时在左侧渲染返回按钮（胶囊底板 + `MiuixIcons.Glass.ChevronBackward`，自动处理 RTL 镜像）。
 * @param titleAlpha 标题透明度，用于「滚动到一定位置才浮现标题」的页面。
 * @param expandable 是否保留可折叠大标题，`false` 时标题固定（等价旧版 SmallTopAppBar）。
 */
@Composable
fun AppTopAppBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    blurActive: Boolean,
    color: Color = MiuixTheme.colorScheme.surface,
    backdrop: LayerBackdrop? = null,
    onBack: (() -> Unit)? = null,
    titleAlpha: () -> Float = { 1f },
    expandable: Boolean = true,
    useGlassTopBar: Boolean = true,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    // 玻璃带区由滚动偏移（contentOffset + overscrollOffset）驱动：内容不足一屏的页面靠
    // pageScrollModifiers 把弹性回弹位移写进 overscrollOffset，因此上滑回弹时同样有带区和模糊。
    val glass = useGlassTopBar && isGlassTopBarAvailable()
    // 宿主（宽屏 + 侧边导航栏）会把这两个值打开：折叠胶囊占据标题位置时标题淡出，且不再展开大标题。
    val chromeAlpha = LocalAppTopBarTitleAlpha.current
    val compact = LocalAppTopBarCompact.current
    val mergedTitleAlpha: () -> Float = { (titleAlpha() * chromeAlpha).coerceIn(0f, 1f) }
    // 宽屏侧边导航栏覆盖在页面上：顶栏前景内容（标题/按钮）让出侧栏宽度，背景带仍铺满全宽。
    val pageStartInset = LocalAppPageStartInset.current
    val backButton: @Composable () -> Unit = {
        CompositionLocalProvider(LocalIsGlassTopBar provides glass) {
            if (onBack != null) {
                BackNavigationButton(
                    onClick = onBack,
                    glass = isGlassTopBarAvailable(),
                    backdrop = backdrop,
                )
            }
            navigationIcon()
        }
    }
    val scopedActions: @Composable RowScope.() -> Unit = {
        CompositionLocalProvider(LocalIsGlassTopBar provides glass) {
            actions()
        }
    }

    if (glass) {
        val surfaceColor = MiuixTheme.colorScheme.surface
        // 与 demo 的 AdaptiveTopAppBar 一致：带区始终使用官方 bandBrush（不透明 surface → 透明的
        // 渐变，配合 28dp 下探渐隐），模糊开关只决定它是否 progressiveBlur。
        // 这里不能退化成 SolidColor(color)：各页在模糊生效时传 Color.Transparent，
        // 会把整条带涂成「什么都没有」，正是「顶栏变透明」的成因。
        val bandBrush = remember(surfaceColor) { GlassTopAppBarDefaults.bandBrush(surfaceColor) }
        val contentScrolled by remember(scrollBehavior) {
            derivedStateOf { scrollBehavior.state.contentOffset < 0f }
        }
        GlassTopAppBar(
            title = title,
            isContentScrolled = contentScrolled,
            // 背景带固定在 y=0（覆盖状态栏），前景内容自己让出状态栏高度。
            contentModifier = Modifier.statusBarsPadding().padding(start = pageStartInset),
            backdrop = backdrop,
            // 带区是否模糊完全跟随「背景模糊」开关（backdrop 为 null 即关闭），与其它页面一致。
            progressiveBlur = backdrop != null,
            scrollBehavior = scrollBehavior,
            expandable = expandable && !compact,
            bandBrush = bandBrush,
            bandOverhang = GlassTopAppBarDefaults.BandOverhang,
            backgroundAlpha = { 1f },
            defaultWindowInsetsPadding = false,
            titleAlpha = mergedTitleAlpha,
            navigationIcon = backButton,
            actions = scopedActions,
        )
    } else {
        BlurredBar(backdrop = backdrop, blurEnabled = blurActive) {
            // 与 demo 的 BlurTopAppBar 一致：标题透明度通过 titleAlpha 作用于「折叠标题 + 大标题」
            // 两个容器（draw 阶段），而不是改 titleColor 的 alpha。改 titleColor 只会让折叠标题
            // 与下方大标题同时可见，短内容页就会出现两个同名标题。
            BlurTopAppBar(
                title = title,
                largeTitleBlurRadius = 12.dp,
                color = color,
                titleColor = MiuixTheme.colorScheme.onSurface,
                largeTitleColor = MiuixTheme.colorScheme.onSurface,
                scrollBehavior = scrollBehavior,
                expandable = expandable && !compact,
                titleAlpha = mergedTitleAlpha,
                navigationIcon = backButton,
                actions = actions,
                modifier = Modifier.padding(start = pageStartInset),
            )
        }
    }
}

/**
 * 二级页面顶栏（带返回按钮），其余与 [AppTopAppBar] 一致。
 */
@Composable
fun AppSubPageTopAppBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    blurActive: Boolean,
    onBack: () -> Unit,
    color: Color = MiuixTheme.colorScheme.surface,
    backdrop: LayerBackdrop? = null,
    expandable: Boolean = true,
    useGlassTopBar: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    AppTopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        blurActive = blurActive,
        color = color,
        backdrop = backdrop,
        onBack = onBack,
        expandable = expandable,
        useGlassTopBar = useGlassTopBar,
        actions = actions,
    )
}

/**
 * 当前是否处于玻璃顶栏的图标插槽内；仅由 [AppTopAppBar] 提供，供 [AppTopAppBarIconAction] 判断按钮样式。
 */
val LocalIsGlassTopBar: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

/**
 * 顶栏标题的额外透明度倍数：宽屏侧栏折叠时顶部胶囊占据标题位置，由宿主把该值降到 0 让标题淡出，
 * 展开成左侧面板后再浮现（对应 miuix example 的 `chromeTitleAlpha`）。
 */
val LocalAppTopBarTitleAlpha: ProvidableCompositionLocal<Float> = staticCompositionLocalOf { 1f }

/**
 * 顶栏是否使用紧凑形态（不展开大标题）：宽屏下由宿主设为 true，
 * 否则下移后的大标题会顶到顶部折叠胶囊上（对应 miuix example 的 `expandable = !isWideScreen`）。
 */
val LocalAppTopBarCompact: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

/**
 * 顶栏动作图标按钮：处于玻璃顶栏内时用 [GlassIconButton]（自动继承顶栏的 style/material/尺寸），
 * 否则退回普通 [IconButton]，让页面无需关心当前是否启用玻璃态。
 */
@Composable
fun AppTopAppBarIconAction(onClick: () -> Unit, content: @Composable () -> Unit) {
    if (LocalIsGlassTopBar.current) {
        GlassIconButton(onClick = onClick, content = content)
    } else {
        IconButton(onClick = onClick, content = content)
    }
}

/**
 * 返回按钮：与 miuix 官方示例 `component/BackNavigationIcon.kt` 完全一致——玻璃顶栏内直接用组件库的
 * [GlassIconButton]（胶囊底板、描边、投影都由它渲染），其余情况用普通 [IconButton]；
 * RTL 下水平镜像箭头。
 *
 * 胶囊是否可见完全由组件库决定：静止时顶栏 `materialProgress ≈ 0`，只显示箭头；
 * 内容滚到顶栏下方或上滑回弹时浮出玻璃胶囊。二级页与「第三方许可证与致谢」页同一条路径，
 * 因此两处的返回按钮表现完全一致。
 */
@Composable
private fun BackNavigationButton(
    onClick: () -> Unit,
    glass: Boolean,
    backdrop: LayerBackdrop? = null,
) {
    val mirrored = LocalLayoutDirection.current == LayoutDirection.Rtl
    val tint = if (glass) MiuixTheme.colorScheme.onSurface else MiuixTheme.colorScheme.onBackground
    val content: @Composable () -> Unit = {
        Icon(
            imageVector = if (glass) MiuixIcons.Glass.ChevronBackward else MiuixIcons.Back,
            contentDescription = stringResource(R.string.action_back),
            tint = tint,
            modifier = Modifier
                .size(if (glass) 28.dp else 24.dp)
                .then(if (mirrored) Modifier.graphicsLayer { scaleX = -1f } else Modifier),
        )
    }
    if (glass) {
        GlassIconButton(
            onClick = onClick,
            backdrop = backdrop,
            content = content,
        )
    } else {
        IconButton(onClick = onClick, content = content)
    }
}

/**
 * 当前环境是否走 OS4 玻璃顶栏：只看运行时着色器支持（Android 13+）。
 *
 * 不再要求「模糊已开启」——关闭模糊时同样走 [GlassTopAppBar]，只是不带 progressiveBlur，
 * 由官方 bandBrush 给出与 demo 一致的「不透明 → 透明」渐变带；否则会退回旧
 * [BlurredBar] + [TopAppBar] 路径，既没有渐变也拿不回状态栏方向的延伸。
 */
internal fun isGlassTopBarAvailable(): Boolean =
    isRuntimeShaderSupported() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

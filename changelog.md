# 更新日志

## 0.8.2

> 发布于 2026-11-21

### 变更

- **miuix 0.9.5-beta03**：同步玻璃顶栏的渐隐和回弹联动。短页面向上越界时，顶栏模糊和按钮材质也会随回弹变化。
- **玻璃弹窗**：顶栏采样正文，弹窗采样整页。弹窗由根容器绘制，关闭模糊时使用实色材质。
- **作用域搜索**：搜索框改用状态化的 `GlassTextField`，保留应用名和包名筛选。

### 优化与修复

- **宽屏导航**：二级页取消主标签高亮。点击侧栏返回主标签时直接切页，避免路由退场动画与切页动画叠加。宽屏布局优先依据应用容器尺寸，适配分屏和自由窗口。
- **设置分组**：分组卡片按行按需加载，保留整组圆角并减少模糊采样时的行间接缝。
- **背景模糊**：「关于」页的装饰模糊跟随全局开关。批量重启对话框的列表底色随浅色和深色主题调整。

## 0.8.1

> 发布于 2026-11-20

### 变更

- **miuix 0.9.5-beta02**：引入新版玻璃组件的动态取色、顶栏布局和模糊渲染改进。导航、操作与搜索图标统一使用 miuix Glass 图标集。
- **横屏二级页**：功能详情、作用域、安全模式和许可证页接入主导航。进入二级页时持续显示顶部胶囊或左侧面板，展开或收起时，顶栏和正文随导航栏实时调整。
- **宽屏搜索**：改用官方 `GlassSearchOverlay`。搜索时可以切换顶部胶囊与左侧面板，搜索框和结果随导航栏让位并保留已输入的关键词。点击导航项会关闭搜索并切换页面。结果滚动时显示渐进模糊，遵循应用的背景模糊设置。
- **玻璃输入框**：文本、数值和包名列表对话框使用官方 `GlassTextField`，输入框底色半透明并随主题变化。
- **默认界面**：使用悬浮底栏、宽屏左侧面板和背景模糊。已保存的设置会继续生效。
- **设置精简**：安全模式页直接显示应用列表，移除搜索条。设置页「模块」分组移除操作项下方的重复说明。

### 优化

- **侧栏形变**：侧栏留白在布局阶段读取动画进度，标题透明度在绘制阶段读取。离屏的相邻页面保持原有边距，减少切换期间的重组和测量。

### 修复

- 液态玻璃底栏避开横屏开孔与侧边导航区域，并修复自由窗口中导航栏和窗口栏底部间距重复叠加的问题。

## 0.8.0

> 发布于 2026-11-19

### 新增

- **查看当前更新日志**：设置页「更新」分组新增入口，可离线查看已安装版本的更新日志。
- **HyperOS 4 玻璃组件**：升级到 miuix 最新主干（含尚未上架 Maven Central 的 `miuix-glass` / `miuix-material` / `miuix-glass-icons`），全面改用 OS4 玻璃材质组件；`RuntimeShader` / `asBrush` / `isRuntimeShaderSupported` / `EdgeStroke` 统一迁移到 `top.yukonga.miuix.kmp.material`，`miuix-shader` 模块已并入 blur/material。
- **玻璃顶栏 `AppTopAppBar`**：新增 `ui/component/AppTopAppBar.kt`，按运行时着色器可用性选择 `GlassTopAppBar`（采样页面 `backdrop`，滚动时标题与着色带渐显、支持展开/收起大标题、带区渐变取自 `GlassTopAppBarDefaults.bandBrush`）或回退到 `BlurredBar` + `BlurTopAppBar`；同时提供 `AppSubPageTopAppBar`、`AppTopAppBarIconAction`、`LocalIsGlassTopBar`。二级页与其它页面走同一条玻璃顶栏路径。
- **页面级顶栏 backdrop**：`BlurUtils.kt` 新增 `rememberTopBarBackdrop(blurActive)`，各页面自建顶栏采样层，并把 `Modifier.layerBackdrop` 施加在**不含顶栏的内容容器**上；顶栏据此折射页面真实内容，模糊关闭时返回 `null` 走实色回退。
- **`PageBackdrop` 页面根容器**：一次完成「把页面内容录入采样图层」与「通过 `LocalPreferenceBackdrop` 下发 backdrop」，主页、功能页、设置页、关于页、许可证页与二级页全部改用该容器。
- **悬浮导航拆成两个独立选项**：设置页「界面」新增「底栏样式」与「侧栏样式」两个下拉，平板可组合出更合适的形态。
  - **底栏样式**：`悬浮`（官方 `GlassNavigationBar`，默认）与 `玻璃`（移植自 miuix 官方示例的 iOS 液态玻璃，带镜面折射、动态高光与拖动指示器）。
  - **侧栏样式**：`关闭`、`顶部胶囊`（默认）、`左侧面板`；仅在宽屏生效，启用后由侧栏取代底栏，进入应用时停在所选形态，可点击胶囊上的按钮在胶囊/面板之间切换。
  - 旧的合并枚举 `NavBarStyle` 与 `nav_bar_style` 键自动迁移到新键 `bottom_bar_style` / `rail_style`，0.8.0 之前的 `floating_navbar` / `liquid_glass` / `show_navigation_rail` / `nav_rail_position` 同样兼容。
- **iOS 液态玻璃底栏**：新增 `ui/component/liquid/`（`LiquidGlassNavigationBar`、`Lens`、`InnerShadow`、`Vibrancy`、`CombinedBackdrop`）与 `ui/component/animation/`（`DampedDragAnimation`、`InteractiveHighlight`），文件保留上游版权与 `SPDX-License-Identifier: Apache-2.0` 声明。
- **页面级侧栏留白**：新增 `LocalAppPageStartInset` 与 `Modifier.pageStartInset()`，把侧栏宽度下发到每个页面的内容间距（等价于 miuix 示例的 `rememberAppPageInsets`）。页面根布局与背景仍是全宽，因此侧栏玻璃能连续采样到整片页面内容。
- **模块级玻璃对话框**：新增 `ui/component/AppDialog.kt`（`AppDialog` / `AppDialogButtonRow` / `AppDialogButton`），内部走 `GlassDialog` + `GlassDialogButton`（`Normal`/`Primary`/`Destructive`），并自动取当前页面的 `LocalPreferenceBackdrop` 作为采样源；`SystemRestartConfirmDialog`、`UpdateDialog`、`QuickActionDialog` 与文本、滑块、包名列表三处设置对话框全部改用该封装。
- **全局搜索（复刻示例的两个入口）**：新增 `ui/component/search/`（`AppSearchOverlay` / `AppSearchResults` / `AppSearchState` / `AppSearchIndex`），搜索界面按 miuix 示例 `GlassSearchOverlay.kt` 复刻（400ms 弹簧曲线展开、取消按钮位移/缩放/模糊、跟随键盘、返回键退出），入口在**底部玻璃底栏右侧的搜索圆钮**与**各主页面顶栏的搜索按钮**两处，且绘制在底栏 / 侧栏之上。
- **功能页搜索改为全局搜索**：页面不再内联 `SearchBar`；搜索结果来自 `OptionRegistry`（含子页面内的功能），摘要显示「分区 / 子页面路径」，点击后自动切到功能页并滚动到对应分区或直接打开对应子页面。
  - 底栏搜索入口与顶栏搜索入口都**只在功能页出现**（可搜索的内容都在该页，其它页面不再显示搜索按钮）；
  - 跳转落位后补写顶栏状态（`heightOffset` / `contentOffset`），大标题像手动下滑一样收起并出现渐变模糊；
  - 目标分区卡片播放两次「变灰闪烁」强调位置（`HookSectionCard(flashToken)`）。

### 变更

- **悬浮底栏改用官方 `GlassNavigationBar`**：使用官方 OS4 玻璃底栏（54dp 高、25dp 图标、11sp 标签、指示器外扩 5dp、按压缩放 0.6，自带底部安全区留白），图标换成 `MiuixIcons.Glass.*`（`HomeOutline` / `ListView` / `SettingsOutline` / `Info`）；悬浮底栏描边使用 `material.EdgeStrokes.Middle*` 配 `Modifier.edgeStroke`。
- **功能页与二级页组件统一到 miuix 新版组件**：开关 / 复选改 content-slot 版 `BasicComponent` + `Switch` / `Checkbox`（状态徽标跟随标题渲染），下拉 → `GlassDropdownPreference`（玻璃气泡弹层），单选 → `RadioButtonPreference`，滑条 → `SliderPreference`，文本、数值、包名列表 → `GlassDialog` + `GlassDialogButton`。
- **顶栏统一走 `GlassTopAppBar`**：上游 0.9.5 把 `BlurTopAppBar` 收为 glass 模块内部实现，且玻璃组件在缺少运行时着色器的设备上会自行把 backdrop 视为 `null`、退化成实色带，因此删除 `AppTopAppBar` 里那份并行的 `BlurredBar` + `BlurTopAppBar` 回退实现，只按着色器可用性决定是否传入 backdrop。
- **顶栏图标统一到玻璃样式**：返回按钮按官方示例改为 `GlassIconButton` + `MiuixIcons.Glass.ChevronBackward`（28dp，RTL 自动镜像），非玻璃环境仍用 `IconButton` + `MiuixIcons.Back`（24dp）；功能页右上角刷新动作改用 `AppTopAppBarIconAction`，跟随顶栏玻璃材质。
- **移除 haze 依赖**：渐变模糊全部改用 miuix `drawBackdrop` / `textureBlur` / `progressiveTextureBlur` / `BlurDefaults.blurColors`，`libs.versions.toml` 与 `app/build.gradle.kts` 中的 `haze` 已删除。
- **主页信息卡合并**：为让主页可滚动而拆开的「设备信息」/「系统版本」两张卡重新合并为一张「设备信息」卡，仍保留足够的可滚动高度。
- **功能页顶栏按钮**：右上角从左到右为「搜索功能」「重启应用」，两者相隔 8 dp。
- **宽屏底栏宽度**：参照 miuix 示例 `GlassPage`，两种样式在宽屏上都将导航胶囊收窄至 344 dp 并居中。悬浮样式为独立搜索按钮预留空间，窄屏底栏随可用宽度伸缩。
- **许可证页**：登记 iOS 液态玻璃示例（miuix 官方示例 `component/liquid`，Apache-2.0）；Miuix 版本标注为 `0.9.5-beta01`。

### 修复

- **手机搜索条位置偏低**：参照 miuix 的 GlassPage 示例，搜索条与页面顶栏共用顶部安全区，重叠的状态栏和挖孔留白只计算一次。
- **卡片分组间距**：统一组间留白，修复主页状态卡片与「设备信息」标题之间的间距偏小。
- **更新弹窗 Markdown 渲染**：当前版本日志与新版本发布说明共用 Markdown 渲染，支持标题、列表、强调、行内代码和链接，长日志可滚动查看。
- **项目与更新链接**：应用内项目地址、更新检查和 README 克隆命令指向 `Ianzb/MiuixGuiTemplate`。
- **启动即闪退（RenderNode 栈溢出）**：玻璃顶栏若采样「包含顶栏自身」的图层，会触发 `RenderNode::prepareTreeImpl` 与 `SkiaDisplayList::prepareListAndChildren` 成对无限递归，表现为启动首帧 `SIGSEGV` / `stack pointer is not in a rw map`。现改为顶栏只采样页面内容层（见上条），并在 `pagerContent` 中把整页 backdrop 固定为 `null` 作为安全网。
- **主页没有渐变模糊**：主页内容不足一屏时 `LazyColumn` 无法滚动，`GlassTopAppBar` 的渐进模糊强度由滚动偏移驱动，`ramp` 恒为 0，条带永不出现。现拆卡并新增「快捷入口」卡片，主页可滚动后顶栏渐变模糊/标题渐显恢复正常。
- **侧栏遮挡页面内容**：此前把留白加在 `HorizontalPager` 上，导致侧栏展开/收起过程中与页面内容错位。现改为页面级 inset；且留白来源从 `animateDpAsState(tween(300))` 改为 `GlassNavigationRailState.progress`，与侧栏形变逐帧同步，不再出现面板已展开而内容尚未让位的遮挡。
- **关闭侧栏后页面整体右移留白**：`GlassNavigationRail` 关闭后不再参与组合，它的 `state` 会停留在原来的展开进度上；`sidebarInset` 现在按 `useRail` 归零，侧栏关闭或窄屏改用底部悬浮栏时页面恢复全宽。
- **功能页「自定义后缀」「自定义应用列表」弹窗退回旧版样式**：功能页仅把 backdrop 传给单个组件，`AppDialog` 取到的采样源为 `null`，`GlassDialog(backdrop = null)` 因此退化成不透明材质。改用 `PageBackdrop` 后恢复玻璃样式，与二级页表现一致。
- **二级页内容不足一屏时没有模糊、大标题还会卡在折叠态**：miuix 的 `MiuixScrollBehavior` 默认 `canScroll = { true }`，内容不足一屏时上滑手势先被顶栏的 `onPreScroll` 整段吃掉（折叠大标题），列表拿不到任何位移，松手后大标题停在折叠态，玻璃带区也永不出现。现与官方 demo（`utils/PageUtils.kt`）对齐，在 `pageScrollModifiers` 里把 `overScrollVertical` 的回弹位移写入 `TopAppBarState.overscrollOffset`，并由二级页脚手架按列表 `canScrollForward/Backward` 传入 `canScroll`：短页面上滑直接弹性回弹，回弹过程中玻璃带区与渐变模糊照常出现，松手回弹并恢复大标题。
- **二级页返回按钮**：与「第三方许可证与致谢」页一致，返回按钮就是组件库的 `GlassIconButton` + `MiuixIcons.Glass.ChevronBackward`（28dp、RTL 自动镜像），胶囊材质跟随顶栏 `materialProgress`：静止时为裸箭头，随滚动 / 弹性回弹浮出玻璃胶囊，不再自绘任何按钮底板。
- **Hook 设置卡片徽标位置**：`HookSwitchCard` / `HookCheckboxCard` 从 `SwitchPreference` / `CheckboxPreference` 改为 content-slot 版 `BasicComponent`，状态徽标（可用/不可用等）跟随标题渲染，不再被挤到行尾控件旁。
- **对话框 / 下拉被悬浮底栏遮住**：弹层原本注册在页面自己的 `Scaffold`（`popupHost` 与页面同层、在底栏之下），手机端贴底弹层与下拉气泡会被悬浮底栏压住。现按 miuix 示例的层级，由 `MainActivity` 最外层「只负责弹层」的 `Scaffold` 承载所有浮层（`AppDialog` 与 `GlassDropdownPreference` 统一 `renderInRootScaffold = true`）：弹层与遮罩直接盖在底栏 / 侧栏之上，不需要任何避让垫高，无底栏页面（二级页、许可证页）位置也不受影响。
- **对话框按钮文案折行**：「取消 / 恢复默认 / 确定」三等分时，「恢复默认」在窄屏或大字体下被折成两行（「恢复默 / 认」）。`AppDialogButton` 改为固定单行 + `TextAutoSize` 自动缩字号，放得下时保持原字号，英文 “Restore Default” 同样不再折行。
- **关于页滚到底卡片被推到顶栏下并硬停**：列表末尾的占位用了 `fillParentMaxHeight()`，可滚动距离比「大标题刚好消失」多出一个顶栏内边距，首张卡片被推到顶栏/状态栏下面后才停住。改为按「列表视口 − 顶部内边距」取最小高度，滚到底时首张卡片正好停在顶栏下沿。
- **关于页滚动到「只剩卡片」时的顿挫**：新增两段吸附（大标题 ⇄ 只剩卡片），手势过半后由弹簧动画收到固定锚点，不再停在中间状态；同时玻璃顶栏路径下不再读取 `collapsed`，避免在大标题消失的临界点触发整页重组。

### 注意

- 侧栏样式仅在宽屏（`shouldShowSplitPane()` 为真）生效，窄屏始终使用「底栏样式」。
- iOS 液态玻璃底栏依赖 AGSL 运行时着色器，Android 13 以下自动回退为实色底栏。
- **依赖改用 Maven Central 正式版本**：miuix `0.9.5-beta01` 已把 `miuix-glass` / `miuix-material` / `miuix-glass-icons` 一起发上 Maven Central，`gradle/libs.versions.toml` 由本地快照（`0.9.4-ac0af467-SNAPSHOT`）改回正式版本号，`settings.gradle.kts` 移除 `mavenLocal()`，CI 也不再克隆 miuix 源码执行 `publishToMavenLocal`——任意机器直接 `./gradlew assembleDebug` 即可。该版本 AAR 以 Android 37.2 编译，`compileSdk` 同步改为 `release(37) { minorApiLevel = 2 }`（需安装 SDK Platform 37.2）。

## 0.7.0

> 发布于 2026-10-26

### 新增

- **Hook 状态四色徽标**：功能卡片的生效状态由灰色两态文本改为标题后的 Miuix `Badge`，固定语义为「未生效（红）/ 等待启动（黄）/ 已生效（绿）/ 不可用（灰）」。`HookStatus` 增加 `WAITING` 与 `UNAVAILABLE`；`HookStatusStore` 按模块 `versionCode` 与 `BOOT_COUNT` 隔离证据：开关关闭或从未上报时等待，目标回报失败时未生效，作用域缺失或设备类型不匹配时不可用。
- **Hook 状态上报协议补全**：`HookStatusContract` 增加 `evaluated_keys` 与 `failed_keys`；`BaseLoad` 在安装前标记已评估、安装成功标记已安装、异常或能力不支持标记失败。上报仍为每进程一次，避免多次 Load 把启动误计为崩溃。
- **状态辅助组件**：新增 `HookStatus`、`HookStatusStore`、`HookStatusBadge` 等组件，`HookCards` 的开关 / 复选框卡片在 `OptionSpec.showStatus` 且启用时渲染徽标；`HookOptionSupport.rememberHookStatus` 统一处理作用域与设备类型导致的「不可用」。
- **文档**：`docs/API.md` §4.5 与 `OptionSpec.showStatus` KDoc 补充四态、颜色、重置与限制说明。

### 变更

- **`OptionSpec.showStatus` 语义**：从“启用后展示灰色生效摘要”改为“展示四色状态徽标”；未提供独立 Hook 的子选项不应声明该字段，避免出现假绿或假红。

## 0.6.0

> 发布于 2026-10-18

### 优化

- **液态玻璃底栏整体移植 SukiSU-Ultra**：`ui/component/liquid/LiquidGlassNavigationBar.kt` 按 [SukiSU-Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra)（GPL-3.0；其上游为 compose-miuix-ui 示例 / [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass)，Apache-2.0）的 `FloatingBottomBar` 重写，替换此前的 `IosLiquidGlassNavigationBar`：
  - **折射不再被采样区裁断**：主胶囊背景采样显式设置 `padding = maxOf(padding, 40.dp)`，圆角处的高光与折射有足够取样空间，边缘不再出现硬切；`Lens.kt` 的圆角半径查找修正为 `centeredCoord`（此前用带 padding 的 `coord` 会取错角半径）。
  - **按压即定位**：按下底栏任意位置，指示器胶囊立即吸附到手指所在的标签，松手即切换；取消手势回弹到当前标签（新增 `DampedDragAnimation.onDragCancelled`）。`updateValue` 改为 `UNDISPATCHED` 启动，拖拽识别改走 `Initial` 阶段 + `positionChangeIgnoreConsumed`，不再因事件被消费而中断。
  - **交互高光**：`InteractiveHighlight` 改用 AGSL 着色器光斑（Android 13 以下回退径向渐变）并带弹簧位置动画，按压亮度提升到 0.06 / 0.12。
  - **尺寸与样式**：底栏宽度改为内容自适应（`width(IntrinsicSize.Min)`，每个标签至少 76dp），以 28dp 水平边距居中，不再是整行铺满的宽胶囊；标签图标/文字按 SukiSU-Ultra 的 11sp / 14sp 规格与 64dp 高度；指示器胶囊保持 56dp、按压缩放 78/56，重力高光按 3° 量化以避免抖动。
- **许可证页**：「参考项目」分组新增 SukiSU-Ultra（GPL-3.0）。

## 0.5.5

> 发布于 2026-10-17

### 修复

- **安全模式 · 重启被误判为崩溃循环**：此前每个 Load 各自上报一次生效状态，一次进程启动就会被计成多次「疑似崩溃」，达到阈值即自动跳过该包全部 Hook（表现为被 Hook 的功能整体失效）。现在改由 `XposedEntry` 在本包全部 Load 完成后统一上报一次，关键进程阈值同步提高到连续 3 次；应用内主动重启目标（含「重启系统界面」）后的启动免计，含 `:` 的子进程上报也不计入。
- **system_server 生效状态上报**：没有 `Application` 的进程（system_server）改为退回系统上下文上报，并在系统上下文尚未创建时 5 秒后补发一次，避免启动证据丢失导致安全模式漏计。

### 文档

- 新增「英文文案与专有名词」「隐私保护（公开仓库）」两节，并补充「逆向产物与报告存放」「构建与日志」；检查清单同步更新，`reverse_report/` 忽略规则移入仓库 `.gitignore`。
- 统一版本标注：移除过时的衍生项目版本记录，`versionName` 与 `about_based_on` 更新为 0.5.4。

## 0.5.4

> 发布于 2026-10-12

### 新增

- **主页 Pager 手势放行**：新增 `ui/util/PagerSwipeOverride.kt`（改编自 miuix `PagerGestureUtils`，Apache-2.0）——`Modifier.pagerSwipeOverride` 在被子组件认领的手势内不拦截翻页，`Modifier.claimPagerSwipe` 供滑条等横向拖拽子组件按下时认领
- **`HookOptionsPage` 顶部提示区**：新增 `topContent` 槽位，渲染在搜索栏上方、搜索展开时隐藏，用于放置系统版本要求等说明卡片（见 [接口文档](docs/API.md) 5.4）

### 优化

- **多级依赖链**：`rememberDependencyEnabled` 现在会沿 `dependsOn` 向上检查依赖链（总开关 → 功能开关 → 组件），链上任意一环节未满足即置灰禁用，避免中间开关关闭时下游组件仍可交互
- `HookSliderCard` 补充 `sliderValueLabelRes`（标题与滑块同属一个组件）的规范说明；`hook` 模块补齐 JVM 单测依赖（`libs.junit` + 真实 `org.json`），便于为 Hook 侧纯逻辑编写单元测试

### 修复

- **Root 权限不再反复申请**：去掉主页「已激活但无 Root」时每 3 秒一次的定时轮询，以及每次回到前台都检测一次的逻辑；现在只在应用启动（`XposedServiceManager.init`）与用户点按主页状态卡片时各检测一次，避免无权限时反复弹出授权窗口、有权限时反复弹出权限使用提示

## 0.5.3

> 发布于 2026-10-04

### 优化

- **安装包体积大幅精简**：Release 开启 R8 代码与资源压缩、Debug/Release 的 dex 采用传统压缩打包。注意：libxposed 模块直接开 R8 时 `compileOnly` 的 API 对 R8 不可见，若用 Kotlin SAM Lambda 实现 `Hooker`，会被编译成 invokedynamic（`$$ExternalSyntheticLambda`，keep 规则无法匹配）而被 R8 破坏，导致 `AbstractMethodError`、hook 全部失效；现改为具名的 `ChainHooker` 实现并整体保留 hook 侧代码，功能不受影响。

### 修复

- **「重启应用」弹窗全选判断失效**：传入的包名列表可能含重复项，`selected`（Set）大小永远小于 `packages.size`，导致「全选」按钮无法切换为「全不选」；现在 `QuickActionDialog` 内部去重并用 `containsAll` 判断。

## 0.5.2

> 发布于 2026-10-01

### 变更

- **安全模式加入白名单**：自动安全模式仅对系统界面、桌面、系统进程（`com.android.systemui` / `com.miui.home` / `android` / `system`）生效；其余进程本身会自行重启，不再计数与触发，避免误判（`SafeModeReader` 与 `SafeModeManager` 同步）

## 0.5.1

> 发布于 2026-09-30

### 新增

- **CI 构建产物推送 Telegram 测试话题**：`CI Build` 工作流构建 Debug APK 并上传 Artifacts 后，自动把测试包推送到 Telegram 群的指定话题；与正式版共用 `CHANNEL_ID` / `BOT_TOKEN`，仅新增 Secret `TEST_MESSAGE_THREAD_ID` 存放话题 `message_thread_id`（`getUpdates` 中的 `message_thread_id`，或话题链接 `https://t.me/c/<群id>/<话题id>` 的末段）。消息带版本号与 Actions 运行链接；未配置该 Secret、缺 `CHANNEL_ID` / `BOT_TOKEN` 或为 `pull_request` 时自动跳过

## 0.5.0

> 发布于 2026-09-29

### 新增

- **统一的「设备独占」API**：`OptionSpec.deviceScope: Set<DeviceType>?` + `rememberOptionEnabled(spec)`（依赖项 ∧ 设备白名单）。仅某类设备可用的功能在其它设备形态上 **禁用灰显、不隐藏**；读取 `ConfigState`，切换「设置 → 当前设备类型」后实时刷新；所有 Hook 卡片统一改用 `rememberOptionEnabled` 作为 `enabled`
- **Hook 生效状态（广播回报）**：目标进程在 `BaseLoad` 完成一组注册后，合并本进程已成功安装的配置键， **一次性定向广播**回报（`HookStatusReporter` / `HookStatusContract`）；App 侧 `HookStatusReceiver` 经「发送者 UID → 包集合 + 模块版本 + 已声明键子集」校验后写入 `HookStatusStore`（device-protected，按 `versionCode` + `BOOT_COUNT` 作用域，旧版本 / 上次开机自动丢弃）；`OptionSpec.showStatus` + `rememberHookApplied(key)` 在副标题展示「已生效 / 未生效」，开关变更后先清证据、待目标进程重启重新回报。不依赖 hook 侧写远程偏好，也无需系统签名级权限

### 变更

- **移除旧「Hook 状态提示（标题染色）」实现，改为下述广播回报机制**：libxposed 远程文件为「App 写、Hook 读」，Hook 侧 `XposedInterface.openRemoteFile` 在 Vector 等新框架上明确为 **只读**，原 `HookStatusWriter` 从 Hook 侧写入必然失败、状态始终为空，故整体移除：删除 `HookStatusWriter` / `HookStatusReader` / `HookStatus` 枚举与示例「状态提示」分区，移除 `OptionSpec.hookId` / `statusId` / `demoStatus`、`rememberHookStatus` / `HookStatusTitleColor`、各卡片 `titleColor` 染色、`hook_status_*` 字符串及 `BaseLoad` / `NativeHookHelper` / `XposedEntry` 中的状态写入调用和相应文档

### 修复

- **安全模式（崩溃循环保护）在 libxposed 上失效**：远程偏好对 hooked app 为 **只读**，原 `SafeModeManager` 从 hook 侧写入必然抛 `UnsupportedOperationException`（被吞后静默失效）。改为：hook 侧仅 **只读** `safe_mode_<pkg>`；App 侧根据「目标进程成功装载 hook」的回报记录启动、窗口内重复启动累计为疑似崩溃，达到阈值（普通 3 / 关键 2）后把 `safe_mode_<pkg>` 回写远程偏好；hook 下次启动读到即跳过全部 hook

## 0.4.3

> 发布于 2026-09-28

### 新增

- **隐藏桌面图标**：设置页「界面」分区新增开关，开启后隐藏本模块桌面图标；桌面图标改由 `activity-alias`（`LauncherAlias`）提供，`MainActivity` 保留 Xposed 模块设置入口（`MODULE_SETTINGS`），隐藏后仍可从模块管理器进入应用

## 0.4.2

> 发布于 2026-09-27

### 新增

- **作用域页 / 安全模式页支持搜索**：两页顶部新增搜索框，按应用名 / 包名实时过滤列表；无匹配时显示空态

## 0.4.1

> 发布于 2026-09-26

### 新增

- **重启成功提示**：`QuickActionDialog` 批量重启完成后弹出 `quick_action_restart_success`（「重启成功」），缺少 Root 时仍提示 `scope_restart_need_root`；同步更新 `docs/API.md` 5.6 与 `docs/CUSTOMIZE.md` 7.6

## 0.4.0

> 发布于 2026-09-25

### 新增

- **原生 Hook 二次封装**：新增 `NativeHookHelper` / `BaseNativeHook` 与 `BaseLoad.initNativeHook`，与 JavaHook 对称的一键接入（声明 / 加载 / 开关 / 状态 / 安全模式），面向 Rust 应用（`flutter_rust_bridge` / 纯 Rust 库）；`rusthook` 统一更名为 `nativehook`
- **版本 / 设备筛选**：新增 `HookVersionGate`（`hook/rule`）与 `hook/device`（`DeviceType`、`DeviceContext`）。版本支持 `>` `<` 比较与多重规则（AND/OR），可按 Android / HyperOS / MIUI / 应用版本；设备支持手机 / 平板 / 折叠屏区分，默认各设备通用；`versionGate` / `deviceScope` / `variants` 可组合使用
- **安全模式页**：设置页「模块」分区新增安全模式声明与入口，二级页可逐应用开关安全模式并查看崩溃计数（`SafeModeReader.setSafeMode`）
- **设置页「模块」整合**：设备类型（默认 / 手机 / 平板 / 折叠屏，支持手动覆盖）与安全模式置于模块区域
- **文档**：新增/重写 [原生 Hook 开发指南](docs/NATIVE_HOOK.md)，明确 JavaHook 与 NativeHook 的区分，聚焦 Rust 应用 Hook 全流程；同步更新接口文档
- **子页面搜索（支持多级）**：`HookOptionsPage` 新增 `subPages` / `HookSubPage`，可把子页面内的功能并入功能页搜索；`HookSubPage.subPages` 支持 **递归嵌套**，多级页面深处的功能同样可被搜索直达（搜索结果摘要显示「父 / 子」路径，命中后直接打开对应层级页面，子页面无需再放搜索栏）
- **Miuix 标准显隐动画**：新增 `ui/util/MiuixAnimations.kt`（`MiuixExpandSpec`），组件显示 / 隐藏统一使用 Miuix 弹簧动画（`folmeSpring(damping = 1.0f, response = 0.4f)`）
- **顶栏扩展槽与重启应用**：`HookOptionsPage` / `SubPageScaffold` / `BaseSubPageActivity` 新增 `topBarActions` 扩展槽；新增通用 `QuickActionsAction`（`MiuixIcons.Refresh` 重启图标 → `QuickActionDialog`），统一右上角「重启」入口样式；`QuickActionDialog` 统一为「重启应用」：标题「重启应用」且无小标题，列表用 `Card` 圆角容器，每行 `CheckboxPreference`（对勾在右、默认全选），底部「全选 / 全不选」+「重启」（无勾选时禁用）
- **SystemUI 重启优化**：新增 `AppRestarter.restartSystemUi()`（`pkill -f` → `killall` → `force-stop` 兜底），`restart()` 对 `com.android.systemui` 特判，结束进程由系统自动拉起而不再触发系统重启
- **`BaseHook.target`**：新增 `protected lateinit var target: PackageTarget`，由 `BaseLoad` 在安装前注入，`init()` 内可直接使用 `target.classLoader`
- **CI / Release 工作流**：新增 GitHub Actions `.github/workflows/ci.yml`（Debug 构建 + Artifact）与 `release.yml`（签名 Release + GitHub Release + 可选 Telegram）；`app/build.gradle.kts` 增加基于环境变量的 `signingConfigs.release` 与 arm64-v8a ABI 拆分；`release.keystore` 与 GitHub Secrets 配置方式见 [README · 发布与 CI](README.md#发布与-ci)
- **应用内检查更新**：新增 `UpdateChecker`（请求 GitHub Releases API，比较语义化版本）与 `UpdateDialog`；设置页「更新」分区支持启动自动检查与手动检查，发现新版本弹窗展示当前 / 最新版本与 Release 更新说明，确认后跳转 GitHub Release 下载页；替换原占位 Toast，`AndroidManifest` 增加 `INTERNET` 权限。使用前请把 `UpdateChecker.REPO` 改为自己的仓库（见[二次开发指南](docs/CUSTOMIZE.md)）

### 变更

- **精简包体**：移除体积巨大的 `material-icons-extended`（约 83MB 的类），改用 `material-icons-core`，并在 `ui/icons/StatusIcons.kt` 内联原本使用的 3 个 Rounded 图标（外观不变）。（注意： **不要开启 R8 混淆**，它会破坏 libxposed 模块的加载与 Hook）
- **示例页 → 功能页**：`ui/screen/examples/ExamplesPage.kt` 更名为 `ui/screen/features/FeaturesPage.kt`（`ExamplesPageView` → `FeaturesPageView`、`exampleSpecs` → `featureSpecs`、子页 `ExampleSubPageActivity` → `FeatureSubPageActivity`），标签页「示例」改为「功能」，与真实模块形态一致；功能页小标题改为单语言，`HookSection.titleEn` 仅保留用于示例展示 API 英文组件名
- 搜索文案由「搜索组件」改为「搜索功能」（`search_hint`），同步更新二次开发指南与接口文档
- **许可要求调整**：衍生项目只需在应用内「关于」页保留 `Based on MiuixGuiTemplate <版本号>` 标注， **不再要求**在各自的 `README.md` 中强调；同步更新 README、二次开发指南与示例注释
- 关于页「反馈渠道」文案改为「Telegram 群组」（英文 `Telegram Group`），并强调不要在文档 / 界面中只写「反馈渠道 / 反馈方式」，以免用户看不出是 TG 群组
- **Telegram 话题推送**：`release.yml` 支持可选 Secret `MESSAGE_THREAD_ID`，多话题群（Forum）可指定发布到哪个话题（不填则发默认 / General 话题）
- **移除热重载功能**：删除设置页「全局热重载」、作用域页与重启应用入口中的热重载，以及 `XposedServiceManager.hotReload` / `runningTargets`、`NativeHookHelper.reset`、`PackageTarget.restored`、`XposedEntry` 的 `onHotReloading` / `onHotReloaded` 与 `module.prop` 的 `autoHotReload`；保留「重启」功能（含 SystemUI 重启优化）

### 修复

- **配置备份 `Float` 往返损坏**：字号等 `Float` 配置导出为 JSON 小数后，导入被解析为 `Double` 并被当作字符串写回，导致设置退回默认、且 hook 侧 `getFloat` 抛 `ClassCastException`；`PrefsStore` 增加 `Double → Float` 处理，`HookPrefs` 各 getter 增加类型不匹配兜底
- **导入失败误报成功**：设置页导入时未检查 `ConfigBackup.importJson` 返回值，非法文件也提示「导入成功」；改为按结果显示成功 / 失败，且仅在成功时重载页面

### 致谢

- 设备判定思路参考 HyperCeiler（AGPL-3.0，仅思路参考，未复用其代码）

## 0.3.1

> 发布于 2026-09-25

### 新增

- **模块开发工作流**：新增 `docs/WORKFLOW.md`，规范「指定应用 Hook 实现」与「参考其他模块复刻功能」两类流程，明确真机 `adb` 扫描须经用户逐次授权、只读命令白名单与隐私边界，以及第三方许可证合规与致谢落点

### 变更

- 「第三方许可证」页面更名为「第三方许可证与致谢」，`licenses_header` 文案覆盖非开源参考项
- 修正文档中 `licenses_section_credits` → `licenses_section_refs` 的笔误

## 0.3.0

> 发布于 2026-09-22

### 变更

- **Hook 状态提示改为标题染色**：规则生效时标题显示为绿色、失败为红色、未应用保持默认色；不再在标题左侧显示对号 / 叉号，`HookStatusStartAction` 更名为 `HookStatusTitleColor`（经 `titleColor` 传入，零额外占位）
- **重启目标应用仅在运行时执行**：应用在运行才 `force-stop` 并重新拉起，未运行则不更改、不自动打开应用
- **开源协议调整为 LGPL-3.0**：仓库同时包含 LGPL-3.0 与 Apache-2.0（自 miuix 引入的文件保留原始声明）许可的代码，按传染性要求整体以 LGPL-3.0 授权分发；新增根目录 `LICENSE`
- 依赖升级：Miuix `0.9.4-rc01` → `0.9.4`；`miuix-navigation3-ui` 更换为 `miuix-nav`
- 主页 Pager 统一为 **Cross-Axis** 拦截模式（`pagerGestureOverride` + `springAnimateToPage`），列表惯性滚动 / 回弹期间可横滑切页
- 「关于」页许可证入口改为 GNU LGPL v3.0，「第三方许可证」更名「第三方许可证与致谢」并新增参考项目分组

### 新增

- **参考与致谢**：README 与应用内说明本模板的脚手架定位，并致谢参考项目（具体清单见应用内「第三方许可证与致谢」页）
- **Based on 约定**：衍生项目须在 `README.md` 与「关于」页标注 `Based on MiuixGuiTemplate <版本号>`；示例程序已内建该标注（当前为 `Based on MiuixGuiTemplate 0.3.0`）
- `docs/CUSTOMIZE.md` 新增「开源协议与致谢（必读）」核对清单，二次开发可一次性对照完成

## 0.2.0

> 发布于 2026-09-13

### 新增

- **Hook 兜底 / 安全模式**：目标进程重复崩溃时自动禁用该包全部 hook，避免系统应用反复崩溃导致无法开机；关键应用阈值更低，支持在作用域页一键恢复
- **作用域页应用操作**：每个应用提供「热重载」「重启」文本按钮（重启更突出），支持对系统进程重启前二次确认
- **二级页面「包名列表」组件**：输入包名后页面右上角出现「快捷操作」按钮，可批量热重载 / 重启，并提供「全部热重载」「全部重启」；支持通过 `customActionPackages` 注入自定义应用
- 主页「模块状态」同时判断模块启用与 **Root 权限**，无 Root 时自动轮询检测，授予后卡片自动更新
- 二次开发指南 `docs/CUSTOMIZE.md`

### 优化

- 作用域列表实时轮询刷新，无需重新打开页面即可反映授权变化
- 「清空 DexKit 缓存」文案不再标注需要 Root（主页已标注 Root 状态）
- 关于页项目地址 / 反馈渠道改为字符串资源驱动，展示与跳转同源
- **顶栏模糊改用 Haze 实现**：`TopBarBlurConfig` 统一 `BlurRadius` / `SurfaceAlpha` / `FullStrengthFraction` / `ScrollFadeDistance`，渐进遮罩为「顶部满强度 → 底边渐隐」，`BlurredBar` 只接受 `HazeState`
- 模块版本号更新为 `0.2.0`

### 修复

- 修复深色模式冷启动瞬间白屏闪烁（新增 `values-night` 窗口背景并兼容手动强制深浅色）
- 修复作用域页刷新与打开动画冲突导致动画丢失
- 修复非作用域内应用无法加载应用信息（新增 `QUERY_ALL_PACKAGES` 权限）
- 修复二级页面（作用域页 / 示例二级页）顶栏不随滚动收起
- **修复顶栏模糊下卡片等硬边缘透出**：Haze 会先原样绘制来源内容再叠加模糊副本，模糊层必须用不透明 `backgroundColor`（surface）作为底层，否则卡片边缘会从模糊中透出，看起来像「组件盖在模糊之上」
- 实现 Android 自动备份规则，去除模板遗留 TODO

## 0.1.0

> 发布于 2026-09-11

### 新增

- 基于 **libxposed API 102** 的完整二次封装：`hookBefore` / `hookAfter` / `hookReplace` / `intercept` / `findAndHook*` / `hookAll*` / `hookClassInitializer` / `invokeOriginal`，统一 `HookRegistry` 管理句柄
- 适配新版热重载机制：`onHotReloading` 保存状态、`onHotReloaded` 重装 hook
- 主动申请作用域：启用选项时通过 `XposedService.requestScope` 自动为未授权目标申请
- DexKit 缓存：`DexKitCacheManager` + `JsonFileCache`，带版本失效校验与文件锁，支持 Root 清空缓存
- 统一配置系统：自定义键名、默认值、持久化、跨进程镜像到 LSPosed 远程偏好、JSON 导出导入
- 全局组件搜索：所有组件标题接入搜索
- Miuix 组件库：开关卡片、箭头卡片、下拉卡片、滑块卡片、复选框卡片、单选（右侧复选框）卡片、文本输入卡片
- 组件与 Hook 状态绑定：标题左侧显示对号 / 叉号，未应用时不显示
- 二级页面模板：`BaseSubPageActivity` + `SubPageScaffold`，自动套用主题模式与背景模糊等模块配置，使用系统默认页面切换动画
- 主页：模块激活状态卡片、作用域统计（点击查看应用图标 / 名称 / 包名 / 版本，实时更新）、模块功能总数
- 示例标签页：内置每种组件类型的实例（不含 hook 具体应用代码）

### 优化

- 滑块支持整数 / 小数 / 自定义范围，数值类型说明显示在滑动条上方、当前数值右侧
- 下拉卡片第一项为「默认（中速）」等默认项，表示不 hook
- 单选卡片改为右侧复选框表示选中项
- 滑块 / 文本输入弹窗内当前值与默认值同一行左右显示，滑块弹窗两侧显示最小值 / 最大值
- 关于页图标运行时读取应用启动图标，与桌面图标保持同步

### 修复

- 修复示例页配置键不匹配导致的启动闪退
- 修复作用域列表应用图标被错误着色的问题

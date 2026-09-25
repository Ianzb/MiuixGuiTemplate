# 更新日志

## 0.4.0

> 发布于 2026-09-25

### 新增

- **原生 Hook 二次封装**：新增 `NativeHookHelper` / `BaseNativeHook` 与 `BaseLoad.initNativeHook`，与 JavaHook 对称的一键接入（声明 / 加载 / 开关 / 状态 / 安全模式），面向 Rust 应用（`flutter_rust_bridge` / 纯 Rust 库）；`rusthook` 统一更名为 `nativehook`
- **版本 / 设备筛选**：新增 `HookVersionGate`（`hook/rule`）与 `hook/device`（`DeviceType`、`DeviceContext`）。版本支持 `>` `<` 比较与多重规则（AND/OR），可按 Android / HyperOS / MIUI / 应用版本；设备支持手机 / 平板 / 折叠屏区分，默认各设备通用；`versionGate` / `deviceScope` / `variants` 可组合使用
- **安全模式页**：设置页「模块」分区新增安全模式声明与入口，二级页可逐应用开关安全模式并查看崩溃计数（`SafeModeReader.setSafeMode`）
- **设置页「模块」整合**：设备类型（默认 / 手机 / 平板 / 折叠屏，支持手动覆盖）与安全模式置于模块区域
- **文档**：新增/重写 [原生 Hook 开发指南](docs/NATIVE_HOOK.md)，明确 JavaHook 与 NativeHook 的区分，聚焦 Rust 应用 Hook 全流程；同步更新接口文档
- **子页面搜索（支持多级）**：`HookOptionsPage` 新增 `subPages` / `HookSubPage`，可把子页面内的功能并入功能页搜索；`HookSubPage.subPages` 支持**递归嵌套**，多级页面深处的功能同样可被搜索直达（搜索结果摘要显示「父 / 子」路径，命中后直接打开对应层级页面，子页面无需再放搜索栏）
- **Miuix 标准显隐动画**：新增 `ui/util/MiuixAnimations.kt`（`MiuixExpandSpec`），组件显示 / 隐藏统一使用 Miuix 弹簧动画（`folmeSpring(damping = 1.0f, response = 0.4f)`）
- **顶栏扩展槽与重启应用**：`HookOptionsPage` / `SubPageScaffold` / `BaseSubPageActivity` 新增 `topBarActions` 扩展槽；新增通用 `QuickActionsAction`（`MiuixIcons.Refresh` 重启图标 → `QuickActionDialog`），统一右上角「重启」入口样式；`QuickActionDialog` 统一为「重启应用」：标题「重启应用」且无小标题，列表用 `Card` 圆角容器，每行 `CheckboxPreference`（对勾在右、默认全选），底部「全选 / 全不选」+「重启」（无勾选时禁用）
- **SystemUI 重启优化**：新增 `AppRestarter.restartSystemUi()`（`pkill -f` → `killall` → `force-stop` 兜底），`restart()` 对 `com.android.systemui` 特判，结束进程由系统自动拉起而不再触发系统重启
- **`BaseHook.target`**：新增 `protected lateinit var target: PackageTarget`，由 `BaseLoad` 在安装前注入，`init()` 内可直接使用 `target.classLoader`
- **CI / Release 工作流**：新增 GitHub Actions `.github/workflows/ci.yml`（Debug 构建 + Artifact）与 `release.yml`（签名 Release + GitHub Release + 可选 Telegram）；`app/build.gradle.kts` 增加基于环境变量的 `signingConfigs.release` 与 arm64-v8a ABI 拆分；`release.keystore` 与 GitHub Secrets 配置方式见 [README · 发布与 CI](README.md#发布与-ci)

### 变更

- **示例页 → 功能页**：`ui/screen/examples/ExamplesPage.kt` 更名为 `ui/screen/features/FeaturesPage.kt`（`ExamplesPageView` → `FeaturesPageView`、`exampleSpecs` → `featureSpecs`、子页 `ExampleSubPageActivity` → `FeatureSubPageActivity`），标签页「示例」改为「功能」，与真实模块形态一致；功能页小标题改为单语言，`HookSection.titleEn` 仅保留用于示例展示 API 英文组件名
- 搜索文案由「搜索组件」改为「搜索功能」（`search_hint`），同步更新二次开发指南与接口文档
- **许可要求调整**：衍生项目只需在应用内「关于」页保留 `Based on MiuixGuiTemplate <版本号>` 标注，**不再要求**在各自的 `README.md` 中强调；同步更新 README、二次开发指南与示例注释
- 关于页「反馈渠道」文案改为「Telegram 群组」（英文 `Telegram Group`），并强调不要在文档 / 界面中只写「反馈渠道 / 反馈方式」，以免用户看不出是 TG 群组
- **Telegram 话题推送**：`release.yml` 支持可选 Secret `MESSAGE_THREAD_ID`，多话题群（Forum）可指定发布到哪个话题（不填则发默认 / General 话题）
- **移除热重载功能**：删除设置页「全局热重载」、作用域页与重启应用入口中的热重载，以及 `XposedServiceManager.hotReload` / `runningTargets`、`NativeHookHelper.reset`、`PackageTarget.restored`、`XposedEntry` 的 `onHotReloading` / `onHotReloaded` 与 `module.prop` 的 `autoHotReload`；保留「重启」功能（含 SystemUI 重启优化）

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

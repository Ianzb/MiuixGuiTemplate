# 更新日志

## 0.2.1

> 发布于 2026-09-22

### 变更

- **开源协议调整为 LGPL-3.0**：仓库同时包含 LGPL-3.0 与 Apache-2.0（自 miuix 引入的文件保留原始声明）许可的代码，按传染性要求整体以 LGPL-3.0 授权分发；新增根目录 `LICENSE`
- 依赖升级：Miuix `0.9.4-rc01` → `0.9.4`；`miuix-navigation3-ui` 更换为 `miuix-nav`
- 主页 Pager 统一为 **Cross-Axis** 拦截模式（`pagerGestureOverride` + `springAnimateToPage`），列表惯性滚动 / 回弹期间可横滑切页
- 「关于」页许可证入口改为 GNU LGPL v3.0，「第三方许可证」更名「第三方许可证与致谢」并新增参考项目分组

### 新增

- **参考与致谢**：README 与应用内说明本模板的脚手架定位，并致谢参考项目 [HyperCeiler](https://github.com/ReChronoRain/HyperCeiler)、[HyperLight](https://github.com/KiminonawaResa/HyperLight)、[miuix](https://github.com/compose-miuix-ui/miuix)
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

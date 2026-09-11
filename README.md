<div align="center">

# MiuixGuiTemplate

### 基于 Miuix 的 LSPosed 模块模板

[接口文档](docs/API.md) | [二次开发指南](docs/CUSTOMIZE.md) | [更新日志](changelog.md)

![Platform](https://img.shields.io/badge/Platform-Android-green)
![LSPosed](https://img.shields.io/badge/LSPosed-libxposed%20102-blue)

</div>

**MiuixGuiTemplate** 是一个开箱即用的 **LSPosed 模块模板**，基于 [libxposed API 102](https://libxposed.github.io/api/index-all.html) 与 [Miuix](https://github.com/YuKongA/Miuix) Compose 组件库，提供一套完整的 Hook 二次封装接口与可复用 UI 组件，帮助你快速构建自己的 Xposed 模块。

<br>

# 功能

- **Hook 封装** — `hookBefore` / `hookAfter` / `hookReplace` / `intercept` / `findAndHook*` / `hookAll*` / `hookClassInitializer` / `invokeOriginal`，统一句柄管理
- **热重载适配** — 基于 libxposed 102 的热重载机制，自动保存状态并在新代码代次重装 hook
- **主动申请作用域** — 启用选项时自动为未授权目标申请作用域
- **DexKit 缓存** — 带 JSON 持久化、版本失效校验与文件锁的 DexKit 缓存，支持 Root 清空
- **统一配置系统** — 自定义键名、默认值、持久化、跨进程镜像、JSON 导出导入
- **组件与 Hook 绑定** — 开关 / 箭头 / 下拉 / 滑块 / 复选框 / 单选 / 文本卡片，标题接入全局搜索
- **Hook 状态展示** — 每个选项标题左侧显示成功（对号）/ 失败（叉号），未应用时不显示
- **二级页面模板** — 独立 Activity，自动套用主题与背景模糊配置
- **示例标签页** — 内置每种组件类型的实例，便于快速上手

<br>

# 系统要求

- 已安装 **LSPosed**（支持 libxposed API 102）
- Android 15+（minSdk 35）
- 清空 DexKit 缓存需要 **Root 权限**

<br>

# 构建

```bash
# 克隆项目
git clone <your-repo-url>
cd MiuixGuiTemplate

# 设置 JDK 17+ 和 Android SDK
# 编辑 local.properties 指向你的 SDK 路径

# 构建 Debug APK
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

<br>

# 快速开始

1. 修改 `app/build.gradle.kts` 与 `settings.gradle.kts` 中的包名 / 应用名。
2. 替换应用图标（`res/drawable/ic_launcher_*.xml` 等）。
3. 修改关于页链接与版权（`strings.xml`）。
4. 在 `:hook` 模块 `META-INF/xposed/scope.list` 中声明作用域包名。
5. 新建 `BaseLoad` 并在 `HookEntryRegistry` 中登记目标包。
6. 新建 `BaseHook` 实现具体 Hook 逻辑。
7. 在 `exampleSpecs()`（或自定义注册处）声明 `OptionSpec`，UI 会自动渲染对应组件。

> **需要修改的完整清单（图标、链接、模块元数据、Hook、配置项等）见 [二次开发指南](docs/CUSTOMIZE.md)。**

详细接口说明见 [接口文档](docs/API.md)。

<br>

# 第三方库

- [Miuix](https://github.com/YuKongA/Miuix) — HyperOS 风格 Compose UI 组件库
- [libxposed API](https://github.com/libxposed/api) — 现代 Xposed 模块 API（102）
- [DexKit](https://github.com/LuckyPray/DexKit) — Dex 解析与缓存
- [AndroidX Compose](https://developer.android.com/jetpack/compose) — 声明式 UI 框架

<br>

# 许可证

本项目基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源。

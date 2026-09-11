# 二次开发指南

本文档说明以本模板为基础开发自己的 LSPosed 模块时，**需要修改的全部内容**：应用标识、图标、链接、模块元数据、Hook 代码、配置项与页面等。

> 约定：本文中的路径均相对于项目根目录。改完建议全局搜索 `cn.ianzb.miuixguitemplate`、`MiuixGuiTemplate`、`miuix_template` 确保没有遗漏。

---

## 0. 必改清单（Checklist）

| 类别 | 位置 | 说明 |
|---|---|---|
| 项目名 | `settings.gradle.kts` → `rootProject.name` | 工程名 |
| 包名 / 应用 ID | `app/build.gradle.kts` → `namespace`、`applicationId` | 应用标识 |
| 包名（hook） | `hook/build.gradle.kts` → `namespace` | 与主包保持一致的前缀 |
| 源码包路径 | `app/src/main/java/...`、`hook/src/main/java/...` | 目录与 `package` 声明同步修改 |
| 版本号 | `app/build.gradle.kts` → `versionCode`、`versionName` | |
| 应用名 | `res/values/strings.xml`、`res/values-en/strings.xml` → `app_name` | |
| 应用图标 | `res/drawable/ic_launcher_*.xml`、`res/mipmap-anydpi*/ic_launcher*.xml`、`colors.xml` → `ic_launcher_background` | |
| 关于页链接 | `strings.xml` → `about_source_code_summary`、`about_telegram_summary` | 显示与跳转均使用该值 |
| 版权 | `strings.xml` → `copyright` | |
| 许可证列表 | `ui/screen/about/LicensePage.kt` → `licenseSections` | 增删依赖库 |
| 模块元数据 | `hook/src/main/resources/META-INF/xposed/{module.prop,scope.list,java_init.list}` | |
| Hook 目标 | `hook/.../base/HookEntryRegistry.kt`、`BaseLoad` 子类 | |
| 配置项 / 页面 | `ui/screen/examples/ExamplesPage.kt`（示例）、`OptionRegistry` | |
| 导出文件名 | `ui/screen/settings/SettingsPage.kt` → `exportLauncher.launch(...)` | |
| 主题 / 颜色 | `res/values/themes.xml`、`res/values/colors.xml` | |
| README / 更新日志 | `README.md`、`changelog.md` | |

---

## 1. 应用标识（包名 / 应用名 / 版本）

### 1.1 Gradle

`settings.gradle.kts`：

```kotlin
rootProject.name = "YourModuleName"
```

`app/build.gradle.kts`：

```kotlin
android {
    namespace = "com.yourname.yourmodule"
    defaultConfig {
        applicationId = "com.yourname.yourmodule"
        versionCode = 1
        versionName = "1.0"
    }
}
```

`hook/build.gradle.kts`：

```kotlin
android {
    namespace = "com.yourname.yourmodule.hook"
}
```

### 1.2 源码包重命名

1. 将 `app/src/main/java/cn/ianzb/miuixguitemplate/` 重命名为 `com/yourname/yourmodule/`；
2. 将 `hook/src/main/java/cn/ianzb/miuixguitemplate/hook/` 重命名为 `com/yourname/yourmodule/hook/`；
3. 全局替换 `cn.ianzb.miuixguitemplate` → `com.yourname.yourmodule`（IDE 的 Rename Package 或全量搜索替换）。

> `AndroidManifest.xml` 中的 Activity 使用相对类名（如 `.TemplateApp`、`.MainActivity`），包名变更后无需手动改。

### 1.3 应用名

`app/src/main/res/values/strings.xml` 与 `values-en/strings.xml`：

```xml
<string name="app_name">YourModuleName</string>
```

---

## 2. 应用图标

模板的启动图标为**自适应图标**，需替换以下资源：

| 文件 | 说明 |
|---|---|
| `res/drawable/ic_launcher_background.xml` | 图标背景层 |
| `res/drawable/ic_launcher_foreground.xml` | 图标前景层 |
| `res/mipmap-anydpi/ic_launcher.xml`、`ic_launcher_round.xml` | 自适应图标描述 |
| `res/mipmap-anydpi-v26/ic_launcher.xml`、`ic_launcher_round.xml` | API 26+ 自适应图标描述 |
| `res/values/colors.xml` → `ic_launcher_background` | 背景色 |

**关于页图标无需单独维护**：`AboutPage.kt` 在运行时通过 `packageManager.getApplicationIcon(...)` 读取启动图标，会自动与桌面图标保持同步。

> 若使用 Android Studio 的 Image Asset 工具生成图标，会覆盖上述文件，无需手工编辑。

---

## 3. 关于页与链接

关于页（`ui/screen/about/AboutPage.kt`）的所有链接现在**均取自字符串资源**，只需修改字符串即可同时更新「显示文本」与「点击跳转」：

`res/values/strings.xml`：

```xml
<string name="about_source_code">项目地址</string>
<string name="about_source_code_summary">https://github.com/yourname/yourmodule</string>
<string name="about_telegram">反馈渠道</string>
<string name="about_telegram_summary">https://t.me/yourchannel</string>
<string name="copyright">© 2026 Your Name.</string>
```

`res/values-en/strings.xml` 同步修改英文文案。

关于页的「Apache License 2.0」跳转链接固定为官方协议地址，如需替换可修改 `AboutPage.kt` 中对应的 `uriHandler.openUri("https://www.apache.org/licenses/LICENSE-2.0.txt")`。

---

## 4. 第三方许可证列表

`ui/screen/about/LicensePage.kt` 顶部的 `licenseSections` 定义了分组与库列表：

```kotlin
private val licenseSections: List<LicenseSection> = listOf(
    LicenseSection(
        titleRes = R.string.licenses_section_core,
        libraries = listOf(
            LibraryInfo("Kotlin", "2.4.20", "Apache-2.0", "https://kotlinlang.org/"),
        ),
    ),
    // ...
)
```

- 增删依赖库时同步维护此列表；
- 分组标题在 `strings.xml` 的 `licenses_section_*` 中定义；
- 库名 / 版本 / 许可证 / 官网为字面量（专有名词无需本地化）。

---

## 5. 模块元数据（LSPosed）

目录：`hook/src/main/resources/META-INF/xposed/`

### 5.1 `java_init.list`

```
com.yourname.yourmodule.hook.xposed.XposedEntry
```

> 仅在入口类所在包变更时修改（默认随包名替换自动更新）。

### 5.2 `module.prop`

```
minApiVersion=102
targetApiVersion=102
autoHotReload=true
staticScope=false
```

> `minApiVersion` / `targetApiVersion` 按需调整；`staticScope=false` 表示支持运行时动态申请作用域（配合 `XposedService.requestScope`）。

### 5.3 `scope.list`

每行一个目标包名，例如：

```
system
com.yourname.targetapp
```

> 该文件声明模块默认作用域，也是「申请作用域」的默认目标来源之一。新增 Hook 目标时务必在此加入对应包名。

---

## 6. Hook 代码

### 6.1 注册目标包

`hook/.../base/HookEntryRegistry.kt`：

```kotlin
private val loads: List<BaseLoad> = listOf(
    MyLoad(),   // 你的 Load
)
```

### 6.2 编写 Load 与 Hook

```kotlin
class MyLoad : BaseLoad() {
    override val targetPackages = listOf("com.yourname.targetapp")
    override fun onPackageLoaded(target: PackageTarget) {
        initHook(MyHook(), HookPrefs.getBoolean(MyHook.KEY, false))
    }
}

class MyHook : BaseHook() {
    override val key = KEY
    override fun init() {
        val clazz = Reflect.findClass("com.yourname.targetapp.Foo", target.classLoader)
        HookHelper.findAndHookMethodAfter(clazz, "bar") { /* ... */ }
    }
    companion object { const val KEY = "targetapp_bar" }
}
```

### 6.3 移除示例 Hook

删除 `hook/.../demo/DemoHook.kt`，并从 `HookEntryRegistry` 中移除 `DemoLoad()`。

### 6.4 配置分组名（保持两端一致）

- `hook/.../prefs/HookPrefs.kt` → `GROUP = "miuix_template_remote"`
- `app/.../prefs/PrefsStore.kt` → `REMOTE_GROUP = "miuix_template_remote"`

> 两个常量**必须一致**，否则 Hook 进程读不到 App 写入的配置。

### 6.5 DexKit 缓存目录（可选）

`hook/.../dexkit/DexKitCacheManager.kt` → `CACHE_DIR = "miuix_template"`。

> 该目录用于清空缓存时的路径拼接，改名后 App 端 `RootHelper.deleteDexKitCache(scope, DexKitCacheManager.CACHE_DIR)` 会自动使用新值。

---

## 7. 配置项与页面

### 7.1 声明配置项

在 `OptionRegistry` 注册（示例见 `ui/screen/examples/ExamplesPage.kt` 的 `exampleSpecs()`）：

```kotlin
OptionSpec(
    key = "targetapp_bar",
    type = OptionType.SWITCH,
    titleRes = R.string.targetapp_bar,
    summaryRes = R.string.targetapp_bar_summary,
    defaultBoolean = false,
    targetPackages = listOf("com.yourname.targetapp"),
)
```

### 7.2 构建页面

使用通用页面组件（详见 [API 文档](API.md) 第 5 节）：

```kotlin
HookOptionsPage(
    title = stringResource(R.string.tab_features),
    sections = listOf(
        HookSection(R.string.section_switch, listOf(spec), "SwitchPreference"),
    ),
    isBlurEnabled = isBlurEnabled,
    extraBottomPadding = extraBottomPadding,
)
```

### 7.3 替换 / 删除示例页

- 示例页 `ui/screen/examples/ExamplesPage.kt` 可整体替换为你的功能页；
- 若删除示例页，请同步更新 `MainActivity.kt` 的标签页列表（当前为 主页 / 示例 / 设置 / 关于）；
- `TemplateApp.onCreate()` 中注册示例配置项的 `OptionRegistry.registerAll(exampleSpecs())` 一并调整。

### 7.4 导出文件名

`ui/screen/settings/SettingsPage.kt`：

```kotlin
exportLauncher.launch("YourModuleName_settings.json")
```

---

## 8. 主题与颜色

| 文件 | 说明 |
|---|---|
| `res/values/themes.xml` | 应用主题（`Theme.MiuixGuiTemplate`） |
| `res/values/colors.xml` | 基础颜色与 `ic_launcher_background` |
| `ui/theme/Theme.kt` | `AppTheme`（Miuix 主题、深浅色、系统栏图标），一般无需修改 |

> 若要改主题名，请同步替换 `AndroidManifest.xml` 与 `themes.xml` 中的 `Theme.MiuixGuiTemplate`。

---

## 9. 其他

| 项目 | 位置 | 说明 |
|---|---|---|
| 应用类 | `TemplateApp.kt` | 初始化配置、服务绑定；改包名后自动生效 |
| 检查更新 | `SettingsPage.kt` / `MainActivity.kt` | 当前为占位（点击仅提示），接入真实更新逻辑时替换 |
| 清空 DexKit 缓存 | `SettingsPage.kt` + `xposed/RootHelper.kt` | 需要 Root，删除作用域应用的 `cache/<CACHE_DIR>` |
| README | `README.md` | 项目简介、链接、构建说明 |
| 更新日志 | `changelog.md` | 按版本记录变更 |
| 接口文档 | `docs/API.md` | 全部对外接口与布局规范 |

---

## 10. 修改后自检

1. 全局搜索 `cn.ianzb.miuixguitemplate`、`MiuixGuiTemplate`、`miuix_template`、`your-name`、`your-channel`，确认无残留。
2. `HookPrefs.GROUP` 与 `PrefsStore.REMOTE_GROUP` 一致。
3. `scope.list` 已包含所有 `BaseLoad.targetPackages`。
4. `META-INF/xposed/java_init.list` 指向正确的入口类。
5. 运行 `./gradlew :hook:compileDebugKotlin :app:assembleDebug` 构建通过。
6. 安装到设备，确认模块在 LSPosed 中被识别、作用域正确、Hook 生效。

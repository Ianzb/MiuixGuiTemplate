pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 本地发布的 miuix（含尚未上架 Maven Central 的 miuix-glass / miuix-material）。
        // 生成方式见 README「OS4 玻璃组件依赖」。仅对 miuix 分组生效，避免污染其他依赖。
        mavenLocal {
            content {
                includeGroup("top.yukonga.miuix.kmp")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "MiuixGuiTemplate"
include(":app")
include(":hook")

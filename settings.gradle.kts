pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                "org.jetbrains.intellij" -> useModule("org.jetbrains.intellij.plugins:gradle-intellij-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "easy-yapi"
include(":common-api", ":idea-plugin", ":plugin-adapter:plugin-adapter-markdown")


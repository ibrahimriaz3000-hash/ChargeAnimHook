pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositories { google(); mavenCentral(); maven { url = uri("https://api.xposed.info/") } }
}
rootProject.name = "ChargeAnimHook"
include(":app")

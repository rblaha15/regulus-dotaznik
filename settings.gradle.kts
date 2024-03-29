@file:Suppress("UnstableApiUsage")

import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://androidx.dev/storage/compose-compiler/repository/")
        }
    }
}

rootProject.name = "Dotazník"
include(":app")

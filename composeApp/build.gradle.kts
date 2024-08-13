import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlin.serialization)
            implementation(libs.koin.compose)
            implementation(libs.kotlinx.time)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.ktor.client.logging)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.cio)
            implementation(libs.sqlite.driver)
        }
    }
}

sqldelight {
    databases {
        create("GithubStats") {
            packageName.set("com.ajcm.jira.cache")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.ajcm.jira"
            packageVersion = "1.0.0"
        }
    }
}

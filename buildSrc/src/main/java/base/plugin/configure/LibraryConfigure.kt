package base.plugin.configure

import AndroidConfig
import base.plugin.internal.libraryExtension
import base.plugin.internal.libs
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
internal fun Project.configureAndroidLibrary() = libraryExtension.run {

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        compileSdk = libs.versions.compileSdk.get().toInt()

        consumerProguardFiles("proguard-rules.pro")
        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        viewBinding = AndroidConfig.VIEW_BINDING_ENABLED
        //  dataBinding = AndroidConfig.DATA_BINDING_ENABLED
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }

        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
    }
}
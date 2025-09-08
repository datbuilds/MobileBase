package base.plugin.configure

import AndroidConfig
import base.plugin.internal.dynamicFeatureExtension
import base.plugin.internal.libs
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
internal fun Project.configureAndroidDynamicFeature() = dynamicFeatureExtension.run {

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        compileSdk = libs.versions.compileSdk.get().toInt()
        proguardFiles("consumer-rules.pro")
        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        viewBinding = AndroidConfig.VIEW_BINDING_ENABLED
//        dataBinding = AndroidConfig.DATA_BINDING_ENABLED
    }
}
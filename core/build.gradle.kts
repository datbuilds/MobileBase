plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_LIBRARY)
    kotlin(Plugins.KOTLIN_KAPT)
}

android {
    namespace = "vn.shb.core"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.data)

    // Core dependencies
    implementation(libs.kotlin)
    implementation(libs.jetPackCrypto)
    implementation(libs.timber)
    
    // Coroutines
    implementation(libs.kotlinxCoroutinesAndroid)

    implementation(libs.easyPermission)
    implementation(libs.networkMonitor)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitGsonConverter)

    //Logging
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipperNetwork)
    debugImplementation(libs.soloader)
    releaseImplementation(libs.flipperNoop)
    implementation(libs.chuckDebugCompile)

    //Room
    implementation(libs.roomKtx)
    implementation(libs.roomRuntime)
    kapt(libs.roomCompiler)

    implementation(platform(libs.okHttpBom))
    implementation(libs.okHttpLogging)
    implementation(libs.okHttp)
    implementation(libs.viewpager2)
    implementation(libs.customviewShimmer)

    implementation(libs.koinAndroid)
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxAppcompat)
    implementation(libs.androidMaterial)
    testImplementation(libs.testingJunit)
    androidTestImplementation(libs.testingAndroidxJunit)
    androidTestImplementation(libs.testingEspressoCore)
}

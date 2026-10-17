plugins {
    id(Plugins.MOBILEBASE_APP)
    id(Plugins.ANDROID_LIBRARY)
    id("kotlin-parcelize")
    alias(libs.plugins.legacy.kapt)
}

android {
    namespace = "com.mobile.data"
}

dependencies {

    // Core dependencies
    implementation(libs.kotlin)
    implementation(libs.timber)
    implementation(libs.gson)

    // Coroutines
    implementation(libs.kotlinxCoroutinesAndroid)

    // AndroidX
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxAppcompat)
    implementation(libs.androidMaterial)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitGsonConverter)

    implementation(platform(libs.okHttpBom))
    implementation(libs.okHttpLogging)
    implementation(libs.okHttp)

    // Room
    implementation(libs.roomKtx)
    implementation(libs.roomRuntime)
    kapt(libs.roomCompiler)

    // Testing
    testImplementation(libs.testingJunit)
    androidTestImplementation(libs.testingAndroidxJunit)
    androidTestImplementation(libs.testingEspressoCore)
}

plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_KOTLIN)
    id(Plugins.ANDROID_LIBRARY)
    kotlin(Plugins.KOTLIN_KAPT)
}

android {
    namespace = "vn.shb.data"
    compileSdk = 35
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
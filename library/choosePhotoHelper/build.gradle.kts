plugins {
    id(Plugins.MOBILEBASE_APP)
    id(Plugins.ANDROID_LIBRARY)
}

android {
    namespace = "com.mobile.choosephotohelper"
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.androidMaterial)
    implementation(libs.androidxAppcompat)
    implementation("androidx.exifinterface:exifinterface:1.3.3")
}

plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_LIBRARY)
}

android {
    namespace = "vn.shb.dn.choosePhotoHelper"
    compileSdk = 35
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.androidMaterial)
    implementation(libs.androidxAppcompat)
    implementation("androidx.exifinterface:exifinterface:1.3.3")
}
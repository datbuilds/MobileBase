plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_LIBRARY)
}

android {
    namespace = "com.example.imagecrouse"
    compileSdk = 35
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.androidMaterial)
    implementation(libs.androidxAppcompat)
    implementation("com.github.bumptech.glide:glide:5.0.5")
}
plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_LIBRARY)
}

dependencies {
    implementation(libs.androidMaterial)
    implementation(projects.localization)
}
plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_LIBRARY)
}

android {
    namespace = "vn.shb.lao.ui"
}

dependencies {
    implementation(libs.androidMaterial)
    implementation(projects.localization)
}
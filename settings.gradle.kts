enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

rootProject.name = "SHB_SAHA_LAOS"

//libraries module
include("library:choosePhotoHelper")

//app
include(":app")
include(":localization")
include(":ui")
include(":data")
include(":shbcore")

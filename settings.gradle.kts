enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

rootProject.name = "MobileBase"

//libraries module
include("library:choosePhotoHelper")
include("library:imagecrouse")

//app
include(":app")
include(":localization")
include(":data")
include(":core")

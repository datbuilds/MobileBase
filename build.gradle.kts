buildscript {
    repositories {
        google()
        maven(url = "https://maven.google.com")
        maven(url = "https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.gradleMavenPushlish)
        classpath(libs.gradlePluginBuildtools)
        classpath(libs.gradlePluginKotlin)
//        classpath(libs.gradlePluginGoogleservices)
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://maven.google.com")
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

tasks {
    registering(Delete::class) {
        delete(buildDir)
    }
}

tasks.withType<JavaCompile> {
    dependsOn("nativeLibsToJar")
}

//apply(from = "../mobile_corp_android/autodimension.gradle")
apply(from = "gradle/projectDependencyGraph.gradle")

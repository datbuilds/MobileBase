plugins {
    id(Plugins.SHB_APP)
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.ANDROID_NAVIGATION)
//    id(Plugins.ANDROID_GOOGLE_SERVICES)
    id(Plugins.ANDROID_KOTLIN)
    id(Plugins.ANDROID_MAVEN_PUBLISH)
    kotlin(Plugins.KOTLIN_KAPT)
}

//apply(from = "autodimension.gradle")

android {
    namespace  = "vn.shb.lao"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions.add(FlavorDimensions.ENVIRONMENT)
    productFlavors {
        createApplicationFlavor(
            pro = {
                resValue("string", "app_name", "SHB SAHA LAOS")
                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://app.shb.com.vn/api/v1/\""
                )
            },
            uat = {
                resValue("string", "app_name", "SHB SAHA LAOS UAT")
                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://uat-app.shb.com.vn/api/v1/\""
                )
            },
            dev = {
                resValue("string", "app_name", "SHB SAHA LAOS DEV")
                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://shb-mobile-lao-gw.shb.com.vn/identyti-service/api/v1/\""
                )
            }
        )
    }

//    aaptOptions {
//        noCompress("bic")
//    }
//
//    packagingOptions {
//        pickFirst("lib/arm64-v8a/libc++_shared.so")
//        pickFirst("lib/x86_64/libc++_shared.so")
//        pickFirst("lib/armeabi-v7a/libc++_shared.so")
//        pickFirst("lib/x86/libc++_shared.so")
//
//        resources {
//            exclude("META-INF/LGPL2.1")
//        }
//    }


    buildFeatures {
        viewBinding = AndroidConfig.VIEW_BINDING_ENABLED
        dataBinding = AndroidConfig.DATA_BINDING_ENABLED
    }

    lint {
        abortOnError = false
        disable.add("Instantiatable")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "$buildDir/libs", "include" to "*.jar")))

    //Import module
    implementation(projects.localization)
    implementation(projects.shbcore)
    implementation(projects.library.choosePhotoHelper)

    //exoPlayer
    implementation(libs.media3Exoplayer)
    implementation(libs.media3ExoplayerUi)

    implementation(libs.androidxWork)

    //AndroidX
    implementation(libs.koinAndroid)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.androidMaterial)
    implementation(libs.androidxBrowser)
    implementation(libs.androidxActivity)
    implementation(libs.androidxAppcompat)
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxConstraintlayout)
    implementation(libs.androidxFragment)
    implementation(libs.androidxLifecycleProcess)
    implementation(libs.androidxNavigationUI)
    implementation(libs.androidxNavigationFragment)
    implementation(libs.easyPermission)
    implementation(libs.networkMonitor)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.biometric)
    implementation(libs.bouncyCastle)
    implementation(libs.androidxSwiperefreshlayout)
    implementation(libs.jetPackCrypto)

    implementation(libs.customviewLottie)
    implementation(libs.imageCompressor)
    implementation(libs.imageCropNew)

    implementation(libs.jodaTime)
    implementation(libs.jodaTimeConvert)

    @Suppress("UnstableApiUsage")
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseMessaging)
    implementation(libs.firebaseAuth)
    implementation(libs.firebaseAnalytics)

    //Room
    implementation(libs.roomKtx)
    implementation(libs.roomRuntime)
    kapt(libs.roomCompiler)

    //Paging
    implementation(libs.androidxPaging)

    //Glide
    implementation(libs.glide)
    kapt(libs.glideCompiler)
    implementation(libs.glideWebpDecoder)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitGsonConverter)

    implementation(platform(libs.okHttpBom))
    implementation(libs.okHttpLogging)
    implementation(libs.okHttp)
    implementation(libs.viewpager2)
    implementation(libs.customviewShimmer)

    //Logging
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipperNetwork)
    debugImplementation(libs.soloader)
    releaseImplementation(libs.flipperNoop)

    debugImplementation(libs.chuckDebugCompile)

    implementation(libs.kotlinReflect)

    //Test
    testImplementation(libs.testingJunit)
    androidTestImplementation(libs.testingAndroidxJunit)
    androidTestImplementation(libs.testingEspressoCore)
}
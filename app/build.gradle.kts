plugins {
    id(Plugins.MOBILEBASE_APP)
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.ANDROID_KOTLIN)
    id(Plugins.ANDROID_MAVEN_PUBLISH)
    kotlin(Plugins.KOTLIN_KAPT)
}

android {
    namespace = "com.mobile.base"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions.add(FlavorDimensions.ENVIRONMENT)
    productFlavors {
        createApplicationFlavor(
            pro = {
                resValue("string", "app_name", "SHB SAHA CAM")
                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://p-apigw-cam.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/\""
                )
            },

            uat = {
                resValue("string", "app_name", "UAT SHB SAHA Cam")

                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://t-apigw-cam.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/\""
                )
            },

            dev = {
                resValue("string", "app_name", "DEV SHB SAHA CAM")

                buildConfigField(
                    "String",
                    "BASE_URL",
                    "\"https://api-gw-ext-dev.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/\""
                )
            }
        )
    }


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
    implementation(projects.core)
    implementation(projects.library.choosePhotoHelper)
    implementation(projects.library.imagecrouse)

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

    //lib
    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.1")
}

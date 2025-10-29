package vn.shb.core.core.security.detectRoot

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Scanner


object RootUtils {

    private val knownRootAppsPackages = arrayOf(
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.zachspong.temprootremovejb",
        "com.ramdroid.appquarantine",
        "eu.chainfire.supersu"
    )
    private val knownDangerousAppsPackages = arrayOf(
        "com.koushikdutta.rommanager",
        "com.koushikdutta.rommanager.license",
        "com.dimonvideo.luckypatcher",
        "com.chelpus.lackypatch",
        "com.ramdroid.appquarantine",
        "com.ramdroid.appquarantinepro"
    )
    private val knownRootCloakingPackages = arrayListOf(
        "com.devadvance.rootcloak",
        "com.devadvance.rootcloakplus",
        "de.robv.android.xposed.installer",
        "com.saurik.substrate",
        "com.zachspong.temprootremovejb",
        "com.amphoras.hidemyroot",
        "com.amphoras.hidemyrootadfree",
        "com.formyhm.hiderootPremium",
        "com.formyhm.hideroot"
    )
    private val suPaths = arrayOf(
        "/data/local/",
        "/data/local/bin/",
        "/data/local/xbin/",
        "/sbin/",
        "/su/bin/",
        "/system/bin/",
        "/system/bin/.ext/",
        "/system/bin/failsafe/",
        "/system/sd/xbin/",
        "/system/usr/we-need-root/",
        "/system/xbin/",
        "/data/local/",
        "/su/xbin/",
        "/magisk/.core/bin/",
        "/system/usr/we-need-root/",
        "/system/xbin/",
    )
    private val pathsThatShouldNotBeWriteable = arrayOf(
        "/system",
        "/system/bin",
        "/system/sbin",
        "/system/xbin",
        "/vendor/bin",
        "/sbin",
        "/etc"
    )
    private val isProbablyRunningOnEmulator: Boolean by lazy {
        return@lazy ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
                ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                        && Build.FINGERPRINT.endsWith(":user/release-keys")
                        && Build.PRODUCT.startsWith("sdk_gphone_")
                        && Build.MODEL.startsWith("sdk_gphone_"))
                        //alternative
                        || (Build.FINGERPRINT.startsWith("google/sdk_gphone64_")
                        && (Build.FINGERPRINT.endsWith(":userdebug/dev-keys") || Build.FINGERPRINT.endsWith(
                    ":user/release-keys"
                ))
                        && Build.PRODUCT.startsWith("sdk_gphone64_")
                        && Build.MODEL.startsWith("sdk_gphone64_"))))
                //
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                //bluestacks
                || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(
            Build.MANUFACTURER,
            ignoreCase = true
        )
                //bluestacks
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST.startsWith("Build")
                //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk")
    }

    @JvmOverloads
    fun detectRootCloakingApps(
        context: Context,
        additionalRootCloakingApps: ArrayList<String> = arrayListOf()
    ): Boolean {
        val packages = ArrayList<String>()
        packages.addAll(knownRootCloakingPackages)
        if (additionalRootCloakingApps.isNotEmpty()) {
            packages.addAll(additionalRootCloakingApps)
        }
        return isAnyPackageFromListInstalled(context, packages)
    }

    private fun checkForSuBinary(): Boolean {
        return checkForBinary("su")
    }

    private fun checkForBusyBoxBinary(): Boolean {
        return checkForBinary("busybox")
    }

    fun isDeviceRooted(context: Context) = (detectRootManagementApps(context)
            || detectPotentiallyDangerousApps(context)
            || checkForBinary("su")
            || checkForBinary("busybox")
            || checkForRWPaths()
            || detectTestKeys()
            || checkSuExists())
            || checkForSuBinary()
            || checkForBusyBoxBinary()
            || isRooted(context)
            || isProbablyRunningOnEmulator

    private fun detectTestKeys(): Boolean {
        val buildTags = Build.TAGS
        val buildFinger = Build.FINGERPRINT
        val product = Build.PRODUCT
        val hardware = Build.HARDWARE
        val display = Build.DISPLAY
        return buildTags != null && (buildTags.contains("test-keys") || buildFinger.contains("genric.*test-keys") || product.contains(
            "generic"
        ) || product.contains("sdk") || hardware.contains("goldfish") || display.contains(".*test-keys"))
    }

    @JvmOverloads
    fun detectRootManagementApps(
        context: Context,
        additionalRootManagementApps: ArrayList<String> = arrayListOf()
    ): Boolean {
        val packages = ArrayList<String>()
        packages.addAll(knownRootAppsPackages)
        if (additionalRootManagementApps.isNotEmpty()) {
            packages.addAll(additionalRootManagementApps)
        }
        return isAnyPackageFromListInstalled(context, packages)
    }

    @JvmOverloads
    fun detectPotentiallyDangerousApps(
        context: Context,
        additionalDangerousApps: ArrayList<String> = arrayListOf()
    ): Boolean {
        val packages = ArrayList<String>()
        packages.addAll(knownDangerousAppsPackages)
        if (additionalDangerousApps.isNotEmpty()) {
            packages.addAll(additionalDangerousApps)
        }
        return isAnyPackageFromListInstalled(context, packages)
    }

    private fun checkForBinary(filename: String): Boolean {
//        val pathsArray = suPaths
//        var result = false
//        for (path in pathsArray) {
//            val completePath = path + filename
//            val f = File(completePath)
//            val fileExists: Boolean = f.exists()
//            if (fileExists) {
//                result = true
//            }
//        }
        return false
    }

    private fun propsReader(): Array<String> {
        var inputstream: InputStream? = null
        try {
            inputstream = Runtime.getRuntime().exec("getprop").inputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var propval = ""
        try {
            propval = Scanner(inputstream).useDelimiter("\\A").next()
        } catch (e: NoSuchElementException) {
        }
        return propval.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    private fun mountReader(): Array<String>? {
        var inputstream: InputStream? = null
        try {
            inputstream = Runtime.getRuntime().exec("mount").inputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (inputstream == null) return null
        var propval = ""
        try {
            propval = Scanner(inputstream).useDelimiter("\\A").next()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
        return propval.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    private fun isAnyPackageFromListInstalled(
        context: Context,
        packages: ArrayList<String>
    ): Boolean {
        var result = false
        val pm = context.packageManager
        for (packageName in packages) {
            result = try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
        return result
    }

    private fun checkForRWPaths(): Boolean {
        var result = false
        val lines = mountReader()
        for (line in lines!!) {
            val args = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (args.size < 4) {
                continue
            }
            val mountPoint = args[1]
            val mountOptions = args[3]
            for (pathToCheck in pathsThatShouldNotBeWriteable) {
                if (mountPoint.equals(pathToCheck, ignoreCase = true)) {
                    for (option in mountOptions.split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()) {
                        if (option.equals("rw", ignoreCase = true)) {
                            result = true
                            break
                        }
                    }
                }
            }
        }
        return result
    }

    private fun checkSuExists(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    // check root shbcn
    private fun isRooted(context: Context): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4(
            context
        )
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private fun checkRootMethod4(context: Context): Boolean {
        return isPackageInstalled("eu.chainfire.supersu", context)
    }

    private fun isPackageInstalled(packageName: String, context: Context): Boolean {
        val pm: PackageManager = context.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // check app chplay installed
    fun isPackageInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES)
            val label = info.applicationInfo?.loadLabel(pm) as String
            label.isNotEmpty() && label.startsWith("Google Play")
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
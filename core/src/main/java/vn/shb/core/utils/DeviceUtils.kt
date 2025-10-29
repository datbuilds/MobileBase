package vn.shb.core.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun isMiUi(): Boolean {
    return !getSystemProperty("ro.miui.ui.version.name").isNullOrEmpty()
}

fun getSystemProperty(propName: String): String? {
    val line: String
    var input: BufferedReader? = null
    try {
        val p = Runtime.getRuntime().exec("getprop $propName")
        input = BufferedReader(InputStreamReader(p.inputStream), 1024)
        line = input.readLine()
        input.close()
    } catch (ex: IOException) {
        return null
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return line
}
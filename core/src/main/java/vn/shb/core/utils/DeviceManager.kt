package vn.shb.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class DeviceManager(private val context: Context) {
    @SuppressLint("HardwareIds")
    fun getDevicesIds(): String? {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}
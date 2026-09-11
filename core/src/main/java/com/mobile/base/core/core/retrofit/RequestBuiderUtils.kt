package com.mobile.base.core.core.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import java.util.Locale
import com.mobile.base.core.utils.AppLanguage


fun Interceptor.Chain.appRequestBuilder(
    versionName: String,
    language: String? = null,
    token: String? = null,
    deviceId: String? = null
) = run {
    val deviceVersion = android.os.Build.VERSION.RELEASE
    val deviceModel = android.os.Build.MODEL
    val deviceManufacturer = android.os.Build.MANUFACTURER
    val resolvedLanguage = AppLanguage.normalize(
        language?.trim()?.takeIf { it.isNotEmpty() } ?: Locale.getDefault().language
    )

    val original = request()
    val originalRequest: Request
    synchronized(this) {
        val requestBuilder = original.newBuilder().apply {
            addHeader("Content-Type", "application/json")
            addHeader("X-Platform", "MOBILE")
            addHeader("X-Device-ID", deviceId.plus("1") ?: "")
            addHeader("X-Language", resolvedLanguage)

            val info = "$versionName(Android$deviceVersion; $deviceModel; $deviceManufacturer"
            val agent = "SHB SAHA Cam App/$info"
            addHeader("User-Agent", "Mozilla/5.0 ($agent)")

            if (token?.isNotEmpty() == true) {
                addHeader("CustomToken", "Bearer $token")
            }

            method(original.method, original.body)
        }
        originalRequest = requestBuilder.build()
    }
    originalRequest
}

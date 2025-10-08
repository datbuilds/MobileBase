package vn.shb.core.core.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import java.util.Locale

fun Interceptor.Chain.appRequestBuilder(
    versionName: String,
    token: String? = null,
    deviceId: String? = null
) = run {
    val deviceVersion = android.os.Build.VERSION.RELEASE
    val deviceModel = android.os.Build.MODEL
    val deviceManufacturer = android.os.Build.MANUFACTURER

    val original = request()
    val originalRequest: Request
    synchronized(this) {
        val requestBuilder = original.newBuilder().apply {
            addHeader("Accept", "*/*")
            addHeader("Content-type", "application/json")
            addHeader("Connection", "keep-alive")
            addHeader("X-Platform", "MOBILE")
            addHeader("X-Device-ID", deviceId ?: "")
            addHeader("X-Language", Locale.getDefault().language)

            val info = "$versionName(Android$deviceVersion; $deviceModel; $deviceManufacturer"
            val agent = "SaleApp/$info"
            addHeader("User-Agent", "Mozilla/5.0 ($agent)")

            val bearer = if (token?.isNotEmpty() == true) {
                "Bearer $token"
            } else {
                ""
            }
            addHeader("Authorization", bearer)

            method(original.method, original.body)
        }
        originalRequest = requestBuilder.build()
    }
    originalRequest
}
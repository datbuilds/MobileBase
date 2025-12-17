package vn.shb.core.core.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import org.koin.android.BuildConfig
import java.util.Locale

fun Interceptor.Chain.appRequestBuilder(
    versionName: String,
    token: String? = null,
    deviceId: String? = null,
    tokenWso2: String? = null,
    isAddWso2: Boolean
) = run {
    val deviceVersion = android.os.Build.VERSION.RELEASE
    val deviceModel = android.os.Build.MODEL
    val deviceManufacturer = android.os.Build.MANUFACTURER

    val original = request()
    val originalRequest: Request
    synchronized(this) {
        val requestBuilder = original.newBuilder().apply {
            addHeader("Content-Type", "application/json")
            addHeader("X-Platform", "MOBILE")
            addHeader("X-Device-ID", deviceId ?: "")
            addHeader("X-Language", Locale.getDefault().language)

            val info = "$versionName(Android$deviceVersion; $deviceModel; $deviceManufacturer"
            val agent = "SHB SAHA Laos App/$info"
            addHeader("User-Agent", "Mozilla/5.0 ($agent)")

            if (token?.isNotEmpty() == true) {
                addHeader("Authorization", "Bearer $token")
            }
            if (!tokenWso2.isNullOrEmpty() && isAddWso2) {
                addHeader("WSO2-Token", "Bearer $tokenWso2")
            }

            method(original.method, original.body)
        }
        originalRequest = requestBuilder.build()
    }
    originalRequest
}
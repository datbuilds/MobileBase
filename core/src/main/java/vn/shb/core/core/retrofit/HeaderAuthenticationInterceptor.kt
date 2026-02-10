package vn.shb.core.core.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import org.koin.android.BuildConfig
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class HeaderAuthenticationInterceptor(
    private val versionName: String,
    private val storage: AndroidSecureStorage,
    private val isProduction : Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Request ban đầu với token hiện tại
        var request =
            chain.appRequestBuilder(
                versionName = versionName,
                token = storage.getToken(),
                deviceId = storage.getDeviceId(),
                tokenWso2 = storage.getTokenWso2(),
                !isProduction
            )

        val response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED || response.code == HTTP_FORBIDDEN) {
            response.close() // đóng response cũ

            synchronized(this) {
                val currentToken = storage.getToken()
                // Chỉ retry nếu token hiện tại trùng với cái mình gửi
                if (request.header("Authorization") == "Bearer $currentToken") {
                    // Chỉ retry với token mới nếu có
                    val newToken = storage.getToken()
                    if (newToken != currentToken) {
                        // Có token mới từ RefreshTokenManager
                        request = chain.appRequestBuilder(
                            versionName = versionName,
                            token = newToken,
                            deviceId = storage.getDeviceId(),
                            tokenWso2 = storage.getTokenWso2(),
                            !isProduction
                        )
                        return chain.proceed(request)
                    } else {
                        // Không có token mới, trả về lỗi
                        println("HeaderAuthenticationInterceptor -> No new token available, returning 401")
                        return response.newBuilder()
                            .code(HTTP_UNAUTHORIZED)
                            .message("Unauthorized - No valid token available")
                            .build()
                    }
                } else {
                    // Có thread khác đã refresh thành công → retry với token mới
                    request = request.newBuilder()
                        .header("Authorization", "Bearer ${storage.getToken()}")
                        .build()
                    return chain.proceed(request)
                }
            }
        }

        return response
    }
}

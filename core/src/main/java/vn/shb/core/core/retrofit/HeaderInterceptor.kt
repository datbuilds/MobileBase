package vn.shb.core.core.retrofit

import okhttp3.Interceptor
import vn.shb.core.core.security.encrypt.AndroidSecureStorage

class HeaderInterceptor(
    private val versionName: String,
    private val storage: AndroidSecureStorage,
    private val isProduction: Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.appRequestBuilder(
            versionName = versionName,
            language = storage.getLanguage(),
            token = storage.getToken(),
            deviceId = storage.getDeviceId(),
            tokenWso2 = storage.getTokenWso2(),
            !isProduction
        )
    )
}

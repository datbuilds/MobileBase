package vn.shb.core.core.retrofit

import okhttp3.Interceptor
import vn.shb.core.core.security.encrypt.AndroidSecureStorage

class Wso2Interceptor(
    private val versionName: String,
    private val storage: AndroidSecureStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.appRequestBuilder(
            versionName = versionName,
            token = storage.getToken(),
            deviceId = storage.getDeviceId(),
            tokenWso2 = storage.getTokenWso2(),
            false
        )
    )
}

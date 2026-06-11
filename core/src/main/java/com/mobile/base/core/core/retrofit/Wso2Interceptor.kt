package com.mobile.base.core.core.retrofit

import okhttp3.Interceptor
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage

class Wso2Interceptor(
    private val versionName: String,
    private val storage: AndroidSecureStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.appRequestBuilder(
            versionName = versionName,
            language = storage.getLanguage(),
            token = storage.getToken(),
            deviceId = storage.getDeviceId(),
            tokenWso2 = storage.getTokenWso2(),
            false
        )
    )
}

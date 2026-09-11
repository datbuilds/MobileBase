package com.mobile.base.core.core.retrofit

import okhttp3.Interceptor
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage

class HeaderInterceptor(
    private val versionName: String,
    private val storage: AndroidSecureStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.appRequestBuilder(
            versionName = versionName,
            language = storage.getLanguage(),
            token = storage.getToken(),
            deviceId = storage.getDeviceId()
        )
    )
}

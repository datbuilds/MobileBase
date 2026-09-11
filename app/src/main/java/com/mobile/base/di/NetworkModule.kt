package com.mobile.base.di

import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import com.mobile.base.core.core.OKHTTP_NORMAL
import com.mobile.base.core.core.RETROFIT_NORMAL
import com.mobile.base.core.core.VERSION_NAME
import com.mobile.base.core.core.domain.source.api.ApiAuth
import com.mobile.base.core.core.domain.source.api.ApiSplash
import com.mobile.base.core.core.domain.source.service.ServiceAuth
import com.mobile.base.core.core.domain.source.service.ServiceSplash
import com.mobile.base.core.core.retrofit.HeaderAuthenticationInterceptor
import com.mobile.base.core.core.retrofit.HeaderInterceptor
import com.mobile.base.core.core.retrofit.loggingInterceptor
import com.mobile.base.core.core.retrofit.okHttpClient
import com.mobile.base.core.core.retrofit.okHttpClientAuthentication
import com.mobile.base.core.core.retrofit.retrofit

fun createNetworkModule(
    baseUrl: String,
    isDebug: Boolean = false,
) = arrayOf(
    // region retrofit base module
    module {

        single<Interceptor>(qualifier = named(OKHTTP_NORMAL)) {
            HeaderInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get()
            )
        }

        single<Interceptor> {
            HeaderAuthenticationInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get()
            )
        }

        single { loggingInterceptor(isDebug) }

        /** AUTHENTICATION interceptor */
        single {
            okHttpClientAuthentication(
                androidContext(),
                get(),
                get(),
            )
        }

        single { retrofit(baseUrl, get()) }

        /** Retrofit not auth */
        single(named(OKHTTP_NORMAL)) {
            okHttpClient(
                androidContext(),
                get(qualifier = named(OKHTTP_NORMAL)),
                get(),
            )
        }

        single(named(RETROFIT_NORMAL)) {
            retrofit(baseUrl, get(named(OKHTTP_NORMAL)))
        }
    },

    // region api service module
    module {
        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiSplash::class.java) }
        factory { ServiceSplash(get()) }

        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiAuth::class.java) }
        factory { ServiceAuth(get()) }
    }
    // endregion
)

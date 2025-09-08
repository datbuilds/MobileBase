package vn.shb.lao.di

import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import vn.shb.core.core.OKHTTP_NORMAL
import vn.shb.core.core.RETROFIT_NORMAL
import vn.shb.core.core.VERSION_NAME
import vn.shb.core.core.domain.source.api.ApiAuth
import vn.shb.core.core.domain.source.api.ApiSplash
import vn.shb.core.core.domain.source.service.ServiceAuth
import vn.shb.core.core.domain.source.service.ServiceSplash
import vn.shb.core.core.retrofit.HeaderAuthenticationInterceptor
import vn.shb.core.core.retrofit.HeaderInterceptor
import vn.shb.core.core.retrofit.loggingInterceptor
import vn.shb.core.core.retrofit.okHttpClient
import vn.shb.core.core.retrofit.okHttpClientAuthentication
import vn.shb.core.core.retrofit.retrofit

fun createNetworkModule(
    baseUrl: String,
    isDebug: Boolean = false,
) = arrayOf(
    // region retrofit base module
    module {
        //        single {
        //            NetworkFlipperPlugin()
        //        }
        //        factory {
        //            FlipperOkhttpInterceptor(
        //                get(), // <-- NetworkFlipperPlugin above
        //                2048L,
        //                true,
        //            )
        //        }

        single<Interceptor>(qualifier = named(OKHTTP_NORMAL)) {
            HeaderInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get(),
            )
        }

        single<Interceptor> {
            HeaderAuthenticationInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get(),
                apiAuth = get(),
            )
        }

        single { loggingInterceptor(isDebug) }

        /** AUTHENTICATION interceptor */
        single {
            okHttpClientAuthentication(
                androidContext(),
                get(),
                get(),
                //                get(),
            )
        }

        single { retrofit(baseUrl, get()) }

        /** Retrofit not auth */
        single(named(OKHTTP_NORMAL)) {
            okHttpClient(
                androidContext(),
                get(qualifier = named(OKHTTP_NORMAL)),
                get(),
                //                get(),
            )
        }

        single(named(RETROFIT_NORMAL)) {
            retrofit(baseUrl, get(named(OKHTTP_NORMAL)))
        }
    },
    // endregion

    // region api service module
    module {
        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiSplash::class.java) }
        factory { ServiceSplash(get()) }

        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiAuth::class.java) }
        factory { ServiceAuth(get()) }
    }
    // endregion
)

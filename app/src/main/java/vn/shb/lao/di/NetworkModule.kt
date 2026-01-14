package vn.shb.lao.di

import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import vn.shb.core.core.OKHTTP_NORMAL
import vn.shb.core.core.OKHTTP_WSO2
import vn.shb.core.core.RETROFIT_NORMAL
import vn.shb.core.core.RETROFIT_WSO2
import vn.shb.core.core.VERSION_NAME
import vn.shb.core.core.domain.source.api.ApiAuth
import vn.shb.core.core.domain.source.api.ApiSplash
import vn.shb.core.core.domain.source.api.ApiTransfer
import vn.shb.core.core.domain.source.api.ApiUser
import vn.shb.core.core.domain.source.api.ApiWSO2
import vn.shb.core.core.domain.source.service.ServiceAuth
import vn.shb.core.core.domain.source.service.ServiceSplash
import vn.shb.core.core.domain.source.service.ServiceTransfer
import vn.shb.core.core.domain.source.service.ServiceUser
import vn.shb.core.core.domain.source.service.ServiceBeneficiary
import vn.shb.core.core.domain.source.service.ServiceWso2
import vn.shb.core.core.retrofit.HeaderAuthenticationInterceptor
import vn.shb.core.core.retrofit.HeaderInterceptor
import vn.shb.core.core.retrofit.Wso2Interceptor
import vn.shb.core.core.retrofit.loggingInterceptor
import vn.shb.core.core.retrofit.okHttpClient
import vn.shb.core.core.retrofit.okHttpClientAuthentication
import vn.shb.core.core.retrofit.retrofit
import vn.shb.lao.BuildConfig

fun createNetworkModule(
    baseUrl: String,
    isDebug: Boolean = false,
) = arrayOf(
    // region retrofit base module
    module {

        single<Interceptor>(qualifier = named(OKHTTP_NORMAL)) {
            HeaderInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get(),
                false
            )
        }

        single<Interceptor>(qualifier = named(OKHTTP_WSO2)) {
            Wso2Interceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get()
            )
        }
        single<Interceptor> {
            HeaderAuthenticationInterceptor(
                versionName = get(qualifier = named(VERSION_NAME)),
                storage = get(),
                false
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

        single(named(OKHTTP_WSO2)) {
            okHttpClient(
                androidContext(),
                get(qualifier = named(OKHTTP_WSO2)),   // interceptor đúng!
                get()                                  // logging interceptor
            )
        }

        single(named(RETROFIT_WSO2)) {
            retrofit(BuildConfig.WSO_URL, get(named(OKHTTP_WSO2)))
        }
    },

    // region api service module
    module {
        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiSplash::class.java) }
        factory { ServiceSplash(get()) }

        factory { get<Retrofit>(named(RETROFIT_NORMAL)).create(ApiAuth::class.java) }
        factory { ServiceAuth(get()) }

        factory { get<Retrofit>().create(ApiUser::class.java) }
        factory { ServiceUser(get()) }
        factory { ServiceBeneficiary(get()) }

        factory { get<Retrofit>().create(ApiTransfer::class.java) }
        factory { ServiceTransfer(get()) }

        factory { get<Retrofit>(named(RETROFIT_WSO2)).create(ApiWSO2::class.java) }
        factory { ServiceWso2(get()) }
    }
    // endregion
)

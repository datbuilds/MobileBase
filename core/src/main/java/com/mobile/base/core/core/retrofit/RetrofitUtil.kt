package com.mobile.base.core.core.retrofit

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.mobile.base.core.BuildConfig
import java.util.concurrent.TimeUnit

const val REQUEST_TIMEOUT = 100L

fun okHttpClient(
    context: Context,
    headerInterceptor: Interceptor,
    loggingInterceptor: HttpLoggingInterceptor,
//    flipperPlugin: NetworkFlipperPlugin
): OkHttpClient {
    val builder=OkHttpClient.Builder()
        .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
    builder.addInterceptor(loggingInterceptor)

    if (BuildConfig.DEBUG) {
        builder.addInterceptor(customChuckerInterceptor(context))
    }

    return builder.build()
}

fun okHttpClientAuthentication(
    context: Context,
    headerInterceptor: Interceptor,
    loggingInterceptor: HttpLoggingInterceptor,
//    flipperPlugin: NetworkFlipperPlugin
): OkHttpClient {

    val builder = OkHttpClient.Builder()
        .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)

    if (BuildConfig.DEBUG) {
        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor(customChuckerInterceptor(context))
    }

    return builder.build()
}


fun customChuckerInterceptor(context: Context) = ChuckerInterceptor.Builder(context)
    .collector(
        ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
    )
    .maxContentLength(250_000L)
    .redactHeaders("Auth-Token", "Bearer")
    .alwaysReadResponseBody(true)
    .build()

fun loggingInterceptor(isDebug: Boolean = false) = HttpLoggingInterceptor().setLevel(
    if (isDebug)
        HttpLoggingInterceptor.Level.BODY
    else
        HttpLoggingInterceptor.Level.NONE
)

fun retrofit(
    baseUrl: String,
    okHttpClient: OkHttpClient
): Retrofit {
    val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

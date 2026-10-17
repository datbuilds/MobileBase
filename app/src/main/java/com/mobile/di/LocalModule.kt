package com.mobile.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.mobile.core.core.local.LocalData
import com.mobile.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.core.core.security.encrypt.EncryptManager
import com.mobile.core.utils.DeviceManager

val localModule = module {

    single<SharedPreferences> {
        get<Context>().getSharedPreferences(
            "kotlincodes", Context.MODE_PRIVATE
        )
    }

    single { DeviceManager(get()) }

    single { EncryptManager.getInstance(get()) }

    single { AndroidSecureStorage(androidContext(), get()) }

    single { LocalData }
}
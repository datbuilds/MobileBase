package com.mobile.base.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.mobile.base.core.core.local.LocalData
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.core.core.security.encrypt.EncryptManager
import com.mobile.base.core.utils.DeviceManager

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
package vn.shb.cam.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import vn.shb.core.core.local.LocalData
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.core.core.security.encrypt.EncryptManager
import vn.shb.core.utils.DeviceManager

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
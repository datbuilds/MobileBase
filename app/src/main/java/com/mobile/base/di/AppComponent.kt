package com.mobile.base.di

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.mobile.base.core.core.VERSION_NAME
import com.mobile.base.BuildConfig
import com.mobile.base.MobileBaseApplication
import com.mobile.base.activity.CountdownViewModel
import com.mobile.base.activity.MainViewModel
import com.mobile.base.di.feature.featureModule

fun appComponent(context: Context) = listOf(
    module {
        factory(qualifier = named(VERSION_NAME)) { BuildConfig.VERSION_NAME }

        factory(qualifier = named("GlobalScope")) { (context as MobileBaseApplication).applicationScope }


        viewModel { MainViewModel() }
        viewModel { CountdownViewModel() }

        single { LocalBroadcastManager.getInstance(get()) }
    },
    *createNetworkModule(BuildConfig.BASE_URL, BuildConfig.DEBUG),
    domainModule,
    localModule,
    repositoryModule,
    // feature module
    featureModule,
)
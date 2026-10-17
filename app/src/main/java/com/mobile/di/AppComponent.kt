package com.mobile.di

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.mobile.core.core.VERSION_NAME
import com.mobile.BuildConfig
import com.mobile.MobileBaseApplication
import com.mobile.activity.CountdownViewModel
import com.mobile.activity.MainViewModel
import com.mobile.di.feature.featureModule

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
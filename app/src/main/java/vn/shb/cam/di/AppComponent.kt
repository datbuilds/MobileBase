package vn.shb.cam.di

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import vn.shb.core.core.VERSION_NAME
import vn.shb.cam.BuildConfig
import vn.shb.cam.SHBApplication
import vn.shb.cam.activity.CountdownViewModel
import vn.shb.cam.activity.MainViewModel
import vn.shb.cam.di.feature.featureModule

fun appComponent(context: Context) = listOf(
    module {
        factory(qualifier = named(VERSION_NAME)) { BuildConfig.VERSION_NAME }

        factory(qualifier = named("GlobalScope")) { (context as SHBApplication).applicationScope }


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
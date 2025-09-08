package vn.shb.lao.di

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import vn.shb.core.core.VERSION_NAME
import vn.shb.lao.BuildConfig
import vn.shb.lao.SHBApplication
import vn.shb.lao.activity.CountdownViewModel
import vn.shb.lao.activity.MainViewModel
import vn.shb.lao.di.feature.featureModule

fun appComponent(context: Context) = listOf(
    module {
        factory(qualifier = named(VERSION_NAME)) { BuildConfig.VERSION_NAME }

        factory(qualifier = named("GlobalScope")) { (context as SHBApplication).applicationScope }


        viewModel { MainViewModel() }
        viewModel { CountdownViewModel() }

        single { LocalBroadcastManager.getInstance(get()) }
    },
    *createNetworkModule(BuildConfig.BASE_URL, BuildConfig.DEBUG),
    databaseModule,
    domainModule,
    localModule,
    repositoryModule,

    // feature module
    featureModule,
)
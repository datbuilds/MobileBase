package com.mobile.base.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.mobile.base.screens.home.HomeViewModel
import com.mobile.base.screens.login.ui.LoginViewModel
import com.mobile.base.screens.splash.ui.SplashViewModel

val featureModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel() }
}

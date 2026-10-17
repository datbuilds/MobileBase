package com.mobile.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.mobile.screens.home.HomeViewModel
import com.mobile.screens.login.ui.LoginViewModel
import com.mobile.screens.splash.ui.SplashViewModel

val featureModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel() }
}

package vn.shb.lao.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.shb.lao.screens.home.HomeViewModel
import vn.shb.lao.screens.login.ui.LoginViewModel
import vn.shb.lao.screens.splash.ui.SplashViewModel

val featureModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel {
        HomeViewModel(
            get(), get(), get(), get(),
            get(), get(), get(), get(), get()
        )
    }
}
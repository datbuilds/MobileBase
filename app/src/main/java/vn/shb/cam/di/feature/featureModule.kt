package vn.shb.cam.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.shb.cam.screens.home.HomeViewModel
import vn.shb.cam.screens.login.ui.LoginViewModel
import vn.shb.cam.screens.profile.ChangePasswordViewModel
import vn.shb.cam.screens.splash.ui.SplashViewModel

import vn.shb.cam.screens.beneficiary.BeneficiaryViewModel

val featureModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel {
        HomeViewModel(
            get(), get(), get(),get(), get(), get(),get(),
            get(),  get(), get(), get()
        )
    }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { BeneficiaryViewModel(get(), get(), get(), get(), get()) }
}
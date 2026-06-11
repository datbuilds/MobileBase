package com.mobile.base.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.mobile.base.screens.paste2pay.ChatPayViewModel
import com.mobile.base.screens.home.HomeViewModel
import com.mobile.base.screens.login.ui.LoginViewModel
import com.mobile.base.screens.profile.ChangePasswordViewModel
import com.mobile.base.screens.splash.ui.SplashViewModel

import com.mobile.base.screens.beneficiary.BeneficiaryViewModel

val featureModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel {
        HomeViewModel(
            get(), get(), get(),get(), get(), get(),get(),
            get(),  get(), get(), get(), get()
        )
    }
    viewModel { ChatPayViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { BeneficiaryViewModel(get(), get(), get(), get(), get(), get()) }
}

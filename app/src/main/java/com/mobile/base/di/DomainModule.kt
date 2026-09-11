package com.mobile.base.di

import org.koin.dsl.module
import com.mobile.base.core.core.domain.usecases.login.RegisterDeviceUseCase
import com.mobile.base.core.core.domain.usecases.login.UseCaseGetSystemVars
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogout
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.domain.usecases.login.VerifyDeviceUseCase

val domainModule = module {
    // refreshToken
    factory { UseCaseLogin(get()) }
    factory { UseCaseGetSystemVars(get()) }

    factory { UseCaseRefreshToken(get()) }

    factory { UseCaseLogout(get()) }
    factory { RegisterDeviceUseCase(get()) }
    factory { VerifyDeviceUseCase(get()) }
}

package com.mobile.base.di

import org.koin.dsl.module
import com.mobile.base.core.core.domain.usecases.login.RegisterDeviceUseCase
import com.mobile.base.core.core.domain.usecases.login.UseCaseGetSystemVars
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogout
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.domain.usecases.login.VerifyDeviceUseCase
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2

val domainModule = module {
    // refreshToken
    factory { UseCaseLogin(get()) }
    factory { UseCaseGetSystemVars(get()) }

    factory { UseCaseRefreshToken(get()) }
    factory { UseCaseRefreshTokenWso2(get()) }

    factory { UseCaseLogout(get()) }
    factory { UseCaseGetTokenWso2(get()) }
    factory { RegisterDeviceUseCase(get()) }
    factory { VerifyDeviceUseCase(get()) }
}
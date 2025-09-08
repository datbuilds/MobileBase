package vn.shb.lao.di

import org.koin.dsl.module
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken

val domainModule = module {
    // refreshToken
    factory { UseCaseLogin(get()) }

    factory { UseCaseRefreshToken(get()) }

    factory { UseCaseLogout(get()) }
}
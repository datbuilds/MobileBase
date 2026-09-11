package com.mobile.base.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.mobile.base.core.core.VERSION_NAME
import com.mobile.base.core.core.domain.source.repository.RepositoryAuthImpl
import com.mobile.base.core.core.domain.source.repository.RepositorySplashImpl
import com.mobile.base.core.core.domain.usecases.login.RepositoryAuth
import com.mobile.base.core.core.domain.usecases.splash.RepositorySplash

val repositoryModule = module {
    factory<RepositorySplash> {
        RepositorySplashImpl(
            get(named(VERSION_NAME)), get(), get()
        )
    }

    factory<RepositoryAuth> { RepositoryAuthImpl(get(), get()) }
}

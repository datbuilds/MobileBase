package com.mobile.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.mobile.core.core.VERSION_NAME
import com.mobile.core.core.domain.source.repository.RepositoryAuthImpl
import com.mobile.core.core.domain.source.repository.RepositorySplashImpl
import com.mobile.core.core.domain.usecases.login.RepositoryAuth
import com.mobile.core.core.domain.usecases.splash.RepositorySplash

val repositoryModule = module {
    factory<RepositorySplash> {
        RepositorySplashImpl(
            get(named(VERSION_NAME)), get(), get()
        )
    }

    factory<RepositoryAuth> { RepositoryAuthImpl(get(), get()) }
}

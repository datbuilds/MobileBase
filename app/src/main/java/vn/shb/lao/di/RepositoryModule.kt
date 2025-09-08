package vn.shb.lao.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import vn.shb.core.core.VERSION_NAME
import vn.shb.core.core.domain.source.repository.RepositoryAuthImpl
import vn.shb.core.core.domain.source.repository.RepositorySplashImpl
import vn.shb.core.core.domain.usecases.login.RepositoryAuth
import vn.shb.core.core.domain.usecases.splash.RepositorySplash

val repositoryModule = module {
    factory<RepositorySplash> {
        RepositorySplashImpl(
            get(named(VERSION_NAME)), get(), get()
        )
    }

    factory<RepositoryAuth> { RepositoryAuthImpl(get(), get()) }
}
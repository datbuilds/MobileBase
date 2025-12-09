package vn.shb.lao.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import vn.shb.core.core.VERSION_NAME
import vn.shb.core.core.domain.source.repository.RepositoryAuthImpl
import vn.shb.core.core.domain.source.repository.RepositorySplashImpl
import vn.shb.core.core.domain.source.repository.RepositoryTokenWsoImpl
import vn.shb.core.core.domain.source.repository.RepositoryTransferImpl
import vn.shb.core.core.domain.source.repository.RepositoryUserImpl
import vn.shb.core.core.domain.usecases.home.RepositoryUser
import vn.shb.core.core.domain.usecases.login.RepositoryAuth
import vn.shb.core.core.domain.usecases.splash.RepositorySplash
import vn.shb.core.core.domain.usecases.transfer.RepositoryTransfer
import vn.shb.core.core.domain.usecases.wso2.RepositoryWSO

val repositoryModule = module {
    factory<RepositorySplash> {
        RepositorySplashImpl(
            get(named(VERSION_NAME)), get(), get()
        )
    }

    factory<RepositoryAuth> { RepositoryAuthImpl(get(), get()) }
    factory<RepositoryUser> { RepositoryUserImpl(get(), get()) }
    factory<RepositoryTransfer> { RepositoryTransferImpl(get(), get()) }
    factory<RepositoryWSO> { RepositoryTokenWsoImpl(get()) }
}
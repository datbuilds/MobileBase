package com.mobile.base.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.mobile.base.core.core.VERSION_NAME
import com.mobile.base.core.core.domain.source.repository.RepositoryAuthImpl
import com.mobile.base.core.core.domain.source.repository.RepositoryAiPayImpl
import com.mobile.base.core.core.domain.source.repository.RepositorySplashImpl
import com.mobile.base.core.core.domain.source.repository.RepositoryTokenWsoImpl
import com.mobile.base.core.core.domain.source.repository.RepositoryTransferImpl
import com.mobile.base.core.core.domain.source.repository.RepositoryUserImpl
import com.mobile.base.core.core.domain.usecases.chatpay.RepositoryChatPay
import com.mobile.base.core.core.domain.usecases.home.RepositoryUser
import com.mobile.base.core.core.domain.usecases.login.RepositoryAuth
import com.mobile.base.core.core.domain.usecases.splash.RepositorySplash
import com.mobile.base.core.core.domain.usecases.transfer.RepositoryTransfer
import com.mobile.base.core.core.domain.usecases.wso2.RepositoryWSO
import com.mobile.base.core.core.domain.usecases.beneficiary.RepositoryBeneficiary
import com.mobile.base.core.core.domain.source.repository.RepositoryBeneficiaryImpl

val repositoryModule = module {
    factory<RepositorySplash> {
        RepositorySplashImpl(
            get(named(VERSION_NAME)), get(), get()
        )
    }

    factory<RepositoryAuth> { RepositoryAuthImpl(get(), get()) }
    factory<RepositoryUser> { RepositoryUserImpl(get(), get()) }
    factory<RepositoryTransfer> { RepositoryTransferImpl(get(), get()) }
    factory<RepositoryChatPay> { RepositoryAiPayImpl(get()) }
    factory<RepositoryBeneficiary> { RepositoryBeneficiaryImpl(get()) }
    factory<RepositoryWSO> { RepositoryTokenWsoImpl(get()) }
}

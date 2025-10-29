package vn.shb.lao.di

import org.koin.dsl.module
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.core.core.domain.usecases.home.UseCaseUserInfo
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.domain.usecases.transfer.UseCaseAccountByNumber
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransfer
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransferAccount

val domainModule = module {
    // refreshToken
    factory { UseCaseLogin(get()) }

    factory { UseCaseRefreshToken(get()) }

    factory { UseCaseLogout(get()) }
    factory { UseCaseUserInfo(get()) }
    factory { UseCaseAccountDetails(get()) }
    factory { UseCaseTransaction(get()) }
    factory { UseCaseTransferAccount(get()) }
    factory { UseCaseAccountByNumber(get()) }
    factory { UseCaseTransactionTransfer(get()) }
    factory { UseCaseTransactionTransferConfirm(get()) }
    factory { UseCaseTransactionDetail(get()) }
}
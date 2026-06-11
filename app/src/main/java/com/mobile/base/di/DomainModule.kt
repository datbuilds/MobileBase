package com.mobile.base.di

import org.koin.dsl.module
import com.mobile.base.core.core.domain.usecases.beneficiary.CreateBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.DeleteBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.GetBanksUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.UpdateBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.ValidateAccountUseCase
import com.mobile.base.core.core.domain.usecases.chatpay.UseCaseAiPay
import com.mobile.base.core.core.domain.usecases.home.UseCaseAccountDetails
import com.mobile.base.core.core.domain.usecases.home.UseCaseSetDefaultAccount
import com.mobile.base.core.core.domain.usecases.home.UseCaseTransaction
import com.mobile.base.core.core.domain.usecases.home.UseCaseUserInfo
import com.mobile.base.core.core.domain.usecases.login.RegisterDeviceUseCase
import com.mobile.base.core.core.domain.usecases.login.UseCaseGetSystemVars
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogout
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.domain.usecases.login.VerifyDeviceUseCase
import com.mobile.base.core.core.domain.usecases.profile.UseCaseChangePassword
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseAccountByNumber
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseExchangeRate
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionTransfer
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseTransferAccount
import com.mobile.base.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2

val domainModule = module {
    // refreshToken
    factory { UseCaseLogin(get()) }
    factory { UseCaseGetSystemVars(get()) }

    factory { UseCaseRefreshToken(get()) }
    factory { UseCaseRefreshTokenWso2(get()) }

    factory { UseCaseLogout(get()) }
    factory { UseCaseUserInfo(get()) }
    factory { UseCaseAccountDetails(get()) }
    factory { UseCaseSetDefaultAccount(get()) }
    factory { UseCaseTransaction(get()) }
    factory { UseCaseAiPay(get()) }
    factory { UseCaseTransferAccount(get()) }
    factory { UseCaseAccountByNumber(get()) }
    factory { UseCaseTransactionTransfer(get()) }
    factory { UseCaseTransactionTransferConfirm(get()) }
    factory { UseCaseTransactionDetail(get()) }
    factory { UseCaseValidateTransaction(get()) }
    factory { GetBeneficiariesUseCase(get()) }
    factory { GetBanksUseCase(get()) }
    factory { DeleteBeneficiaryUseCase(get()) }
    factory { CreateBeneficiaryUseCase(get()) }
    factory { UpdateBeneficiaryUseCase(get()) }
    factory { UseCaseGetTokenWso2(get()) }
    factory { UseCaseChangePassword(get()) }
    factory { ValidateAccountUseCase(get()) }
    factory { RegisterDeviceUseCase(get()) }
    factory { VerifyDeviceUseCase(get()) }
    factory { UseCaseExchangeRate(get()) }
}
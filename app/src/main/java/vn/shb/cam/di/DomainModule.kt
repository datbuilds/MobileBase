package vn.shb.cam.di

import org.koin.dsl.module
import vn.shb.core.core.domain.usecases.beneficiary.CreateBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.DeleteBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.GetBanksUseCase
import vn.shb.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import vn.shb.core.core.domain.usecases.beneficiary.UpdateBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.ValidateAccountUseCase
import vn.shb.core.core.domain.usecases.chatpay.UseCaseAiPay
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseSetDefaultAccount
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.core.core.domain.usecases.home.UseCaseUserInfo
import vn.shb.core.core.domain.usecases.login.RegisterDeviceUseCase
import vn.shb.core.core.domain.usecases.login.UseCaseGetSystemVars
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.domain.usecases.login.VerifyDeviceUseCase
import vn.shb.core.core.domain.usecases.profile.UseCaseChangePassword
import vn.shb.core.core.domain.usecases.transfer.UseCaseAccountByNumber
import vn.shb.core.core.domain.usecases.transfer.UseCaseExchangeRate
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransfer
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransferAccount
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2

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
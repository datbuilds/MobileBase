package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.api.ApiUser
import vn.shb.core.core.domain.source.request.DefaultAccountRequest
import vn.shb.core.core.domain.source.response.DefaultAccountResponse
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.core.core.retrofit.SafeExecute

class ServiceUser(private val api: ApiUser) : SafeExecute() {

    suspend fun getUserInfo() = execute {
        api.getUserInfo().awaitResponse()
    }

    suspend fun getAccountsInfo() = execute {
        api.getAccountsInfo().awaitResponse()
    }

    suspend fun getAccountDetails(params: UseCaseAccountDetails.Params) = execute {
        api.getAccountDetails(params.accountNumber).awaitResponse()
    }

    suspend fun getTransactions(params: UseCaseTransaction.Params) = execute {

        api.getTransactions(
            params.accountNumber, params.queryType, params.fromDate, params.toDate
        ).awaitResponse()
    }

    suspend fun setDefaultAccount(accountNo: String): ResultSHB<DefaultAccountResponse> = execute {
        api.setDefaultAccount(DefaultAccountRequest(accountNo)).awaitResponse()
    }

    suspend fun changePassword(request: vn.shb.core.core.domain.source.request.ChangePasswordRequest): vn.shb.core.core.delivery.ResultSHB<vn.shb.core.core.domain.source.response.ChangePasswordResponse> = execute {
        api.changePassword(request).awaitResponse()
    }
}
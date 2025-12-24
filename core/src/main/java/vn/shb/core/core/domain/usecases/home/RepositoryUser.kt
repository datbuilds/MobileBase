package vn.shb.core.core.domain.usecases.home

import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.ChangePasswordRequest
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.source.response.AccountDetailsData
import vn.shb.core.core.domain.source.response.TransactionData
import vn.shb.data.entities.home.UserInfo

interface RepositoryUser {
    suspend fun getUserInfo(): ResultSHB<UserInfo>

    suspend fun getAccountsInfos(): ResultSHB<AccountData>

    suspend fun getAccountDetails(params: UseCaseAccountDetails.Params): ResultSHB<AccountDetailsData>

    suspend fun getTransactions(params: UseCaseTransaction.Params): ResultSHB<TransactionData>

    suspend fun setDefaultAccount(accountNo: String): ResultSHB<Boolean>

    suspend fun changePassword(request: ChangePasswordRequest): ResultSHB<vn.shb.core.core.domain.source.response.ChangePasswordResponse>
}
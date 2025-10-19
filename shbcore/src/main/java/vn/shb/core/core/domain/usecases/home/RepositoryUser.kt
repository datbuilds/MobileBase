package vn.shb.core.core.domain.usecases.home

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.source.response.AccountDetailsData
import vn.shb.core.core.domain.source.response.TransactionData
import vn.shb.data.entities.home.UserInfo

interface RepositoryUser {
    suspend fun getUserInfo(): ResultSHB<UserInfo>

    suspend fun getAccountsInfos(): ResultSHB<AccountData>

    suspend fun getAccountDetails(params: UseCaseAccountDetails.Params): ResultSHB<AccountDetailsData>

    suspend fun getTransactions(params: UseCaseTransaction.Params): ResultSHB<TransactionData>
}
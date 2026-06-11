package com.mobile.base.core.core.domain.usecases.home

import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.ChangePasswordRequest
import com.mobile.base.core.core.domain.source.response.AccountData
import com.mobile.base.core.core.domain.source.response.AccountDetailsData
import com.mobile.base.core.core.domain.source.response.TransactionData
import com.mobile.base.data.entities.home.UserInfo

interface RepositoryUser {
    suspend fun getUserInfo(): ResultState<UserInfo>

    suspend fun getAccountsInfos(): ResultState<AccountData>

    suspend fun getAccountDetails(params: UseCaseAccountDetails.Params): ResultState<AccountDetailsData>

    suspend fun getTransactions(params: UseCaseTransaction.Params): ResultState<TransactionData>

    suspend fun setDefaultAccount(accountNo: String): ResultState<Boolean>

    suspend fun changePassword(request: ChangePasswordRequest): ResultState<com.mobile.base.core.core.domain.source.response.ChangePasswordResponse>
}
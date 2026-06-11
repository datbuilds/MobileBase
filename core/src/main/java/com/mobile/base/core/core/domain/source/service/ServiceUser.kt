package com.mobile.base.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.api.ApiUser
import com.mobile.base.core.core.domain.source.request.DefaultAccountRequest
import com.mobile.base.core.core.domain.source.response.DefaultAccountResponse
import com.mobile.base.core.core.domain.usecases.home.UseCaseAccountDetails
import com.mobile.base.core.core.domain.usecases.home.UseCaseTransaction
import com.mobile.base.core.core.retrofit.SafeExecute

class ServiceUser(private val api: ApiUser) : SafeExecute() {

    suspend fun getUserInfo() = execute {
        api.getUserInfo()
    }

    suspend fun getAccountsInfo() = execute {
        api.getAccountsInfo()
    }

    suspend fun getAccountDetails(params: UseCaseAccountDetails.Params) = execute {
        api.getAccountDetails(params.accountNumber)
    }

    suspend fun getTransactions(params: UseCaseTransaction.Params) = execute {

        api.getTransactions(
            params.accountNumber, params.queryType, params.fromDate, params.toDate
        )
    }

    suspend fun setDefaultAccount(accountNo: String): ResultState<DefaultAccountResponse> = execute {
        api.setDefaultAccount(DefaultAccountRequest(accountNo))
    }

    suspend fun changePassword(request: com.mobile.base.core.core.domain.source.request.ChangePasswordRequest): com.mobile.base.core.core.delivery.ResultState<com.mobile.base.core.core.domain.source.response.ChangePasswordResponse> = execute {
        api.changePassword(request)
    }
}
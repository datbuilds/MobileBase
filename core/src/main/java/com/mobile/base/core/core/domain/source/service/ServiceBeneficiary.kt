package com.mobile.base.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.base.core.core.delivery.EmptyResponse
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.api.ApiUser
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.request.RemoveBeneficiaryRequest
import com.mobile.base.core.core.domain.source.response.BeneficiaryResponse
import com.mobile.base.core.core.retrofit.SafeExecute

class ServiceBeneficiary(private val api: ApiUser) : SafeExecute() {

    suspend fun getBeneficiaries(): ResultState<BeneficiaryResponse> = execute {
        api.getBeneficiaries()
    }

    suspend fun validateAccount(request: com.mobile.base.core.core.domain.source.request.ValidateAccountRequest): ResultState<com.mobile.base.core.core.domain.source.response.ValidateAccountResponse> = execute {
        api.validateAccount(request)
    }

    suspend fun getBanks() = execute { api.getBanks()}

    suspend fun deleteBeneficiary(request: RemoveBeneficiaryRequest): ResultState<EmptyResponse> = execute {
        api.deleteBeneficiary(request)
    }

    suspend fun createBeneficiary(request: BeneficiaryRequest): ResultState<BeneficiaryResponse> = execute {
        api.createBeneficiary(request)
    }

    suspend fun updateBeneficiary( request: BeneficiaryRequest): ResultState<BeneficiaryResponse> = execute {
        api.updateBeneficiary( request)
    }
}
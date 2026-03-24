package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.delivery.EmptyResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.api.ApiUser
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.request.RemoveBeneficiaryRequest
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.retrofit.SafeExecute

class ServiceBeneficiary(private val api: ApiUser) : SafeExecute() {

    suspend fun getBeneficiaries(): ResultSHB<BeneficiaryResponse> = execute {
        api.getBeneficiaries()
    }

    suspend fun validateAccount(request: vn.shb.core.core.domain.source.request.ValidateAccountRequest): ResultSHB<vn.shb.core.core.domain.source.response.ValidateAccountResponse> = execute {
        api.validateAccount(request)
    }

    suspend fun getBanks() = execute { api.getBanks()}

    suspend fun deleteBeneficiary(request: RemoveBeneficiaryRequest): ResultSHB<EmptyResponse> = execute {
        api.deleteBeneficiary(request)
    }

    suspend fun createBeneficiary(request: BeneficiaryRequest): ResultSHB<BeneficiaryResponse> = execute {
        api.createBeneficiary(request)
    }

    suspend fun updateBeneficiary( request: BeneficiaryRequest): ResultSHB<BeneficiaryResponse> = execute {
        api.updateBeneficiary( request)
    }
}
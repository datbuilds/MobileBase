package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.api.ApiUser
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.retrofit.SafeExecute

class ServiceBeneficiary(private val api: ApiUser) : SafeExecute() {

    suspend fun getBeneficiaries(): ResultSHB<BeneficiaryResponse> = execute {
        api.getBeneficiaries().awaitResponse()
    }

    suspend fun validateAccount(request: vn.shb.core.core.domain.source.request.ValidateAccountRequest): ResultSHB<vn.shb.core.core.domain.source.response.ValidateAccountResponse> = execute {
        api.validateAccount(request).awaitResponse()
    }

    suspend fun getBanks() = execute { api.getBanks().awaitResponse() }

    suspend fun deleteBeneficiary(id: String): ResultSHB<vn.shb.core.core.delivery.EmptyResponse> = execute {
        api.deleteBeneficiary(id).awaitResponse()
    }

    suspend fun createBeneficiary(request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<BeneficiaryResponse> = execute {
        api.createBeneficiary(request).awaitResponse()
    }

    suspend fun updateBeneficiary(id: String, request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<BeneficiaryResponse> = execute {
        api.updateBeneficiary(id, request).awaitResponse()
    }
}
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

    suspend fun getBanks() = execute { api.getBanks().awaitResponse() }
}
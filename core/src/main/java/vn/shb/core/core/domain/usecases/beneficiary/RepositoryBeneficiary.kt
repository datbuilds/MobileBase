package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.data.entities.beneficiary.Beneficiary

interface RepositoryBeneficiary {
    suspend fun getBeneficiaries(): ResultSHB<List<Beneficiary>>
    suspend fun getBanks(): ResultSHB<List<Bank>>
    suspend fun createBeneficiary(request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<vn.shb.core.core.domain.source.response.BeneficiaryResponse>
    suspend fun updateBeneficiary(
        id: String,
        request: BeneficiaryRequest
    ): ResultSHB<BeneficiaryResponse>

    suspend fun deleteBeneficiary(id: String): ResultSHB<ActionDone>
}

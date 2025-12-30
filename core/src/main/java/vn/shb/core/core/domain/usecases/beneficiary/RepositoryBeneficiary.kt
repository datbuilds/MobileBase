package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.data.entities.beneficiary.Beneficiary

interface RepositoryBeneficiary {
    suspend fun getBeneficiaries(): ResultSHB<List<Beneficiary>>
    suspend fun getBanks(): ResultSHB<List<Bank>>
}

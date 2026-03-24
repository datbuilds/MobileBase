package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None
import vn.shb.data.entities.beneficiary.Bank

import kotlinx.coroutines.flow.FlowCollector

class GetBanksUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<List<Bank>, None>() {
    override suspend fun FlowCollector<ResultSHB<List<Bank>>>.run(params: None) {
        emit(repository.getBanks())
    }
}

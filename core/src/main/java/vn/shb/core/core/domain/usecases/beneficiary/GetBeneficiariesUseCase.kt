package vn.shb.core.core.domain.usecases.beneficiary

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None
import vn.shb.data.entities.beneficiary.Beneficiary

class GetBeneficiariesUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<List<Beneficiary>, None>() {
    override suspend fun FlowCollector<ResultSHB<List<Beneficiary>>>.run(params: None) {
        emit(repository.getBeneficiaries())
    }
}

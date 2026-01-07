package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.domain.usecases.BaseUseCase
import kotlinx.coroutines.flow.FlowCollector

class CreateBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<BeneficiaryResponse, BeneficiaryRequest>() {
    override suspend fun FlowCollector<ResultSHB<BeneficiaryResponse>>.run(params: BeneficiaryRequest) {
        emit(repository.createBeneficiary(params))
    }
}

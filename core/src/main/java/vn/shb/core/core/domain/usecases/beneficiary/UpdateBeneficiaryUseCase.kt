package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.domain.usecases.BaseUseCase
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UpdateBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<BeneficiaryResponse, UpdateBeneficiaryUseCase.Params>() {

    data class Params(
        val request: BeneficiaryRequest
    ) : UseCaseParameters

    override suspend fun FlowCollector<ResultSHB<BeneficiaryResponse>>.run(params: Params) {
        emit(repository.updateBeneficiary(params.request))
    }
}

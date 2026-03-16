package vn.shb.core.core.domain.usecases.beneficiary

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.RemoveBeneficiaryRequest
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class DeleteBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<ActionDone, DeleteBeneficiaryUseCase.Params>() {

    override suspend fun FlowCollector<ResultSHB<ActionDone>>.run(params: Params) {
        emit(repository.deleteBeneficiary(params.bodyRequest))
    }

    data class Params(val bodyRequest: RemoveBeneficiaryRequest) : UseCaseParameters
}

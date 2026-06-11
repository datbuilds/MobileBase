package com.mobile.base.core.core.domain.usecases.beneficiary

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.RemoveBeneficiaryRequest
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class DeleteBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<ActionDone, DeleteBeneficiaryUseCase.Params>() {

    override suspend fun FlowCollector<ResultState<ActionDone>>.run(params: Params) {
        emit(repository.deleteBeneficiary(params.bodyRequest))
    }

    data class Params(val bodyRequest: RemoveBeneficiaryRequest) : UseCaseParameters
}

package com.mobile.base.core.core.domain.usecases.beneficiary

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.response.BeneficiaryResponse
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.domain.usecases.UseCaseParameters

class UpdateBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<BeneficiaryResponse, UpdateBeneficiaryUseCase.Params>() {

    data class Params(
        val request: BeneficiaryRequest
    ) : UseCaseParameters

    override suspend fun FlowCollector<ResultState<BeneficiaryResponse>>.run(params: Params) {
        emit(repository.updateBeneficiary(params.request))
    }
}

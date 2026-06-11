package com.mobile.base.core.core.domain.usecases.beneficiary

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.response.BeneficiaryResponse
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import kotlinx.coroutines.flow.FlowCollector

class CreateBeneficiaryUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<BeneficiaryResponse, BeneficiaryRequest>() {
    override suspend fun FlowCollector<ResultState<BeneficiaryResponse>>.run(params: BeneficiaryRequest) {
        emit(repository.createBeneficiary(params))
    }
}

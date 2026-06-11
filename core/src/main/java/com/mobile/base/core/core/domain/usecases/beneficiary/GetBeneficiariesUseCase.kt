package com.mobile.base.core.core.domain.usecases.beneficiary

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.data.entities.beneficiary.Beneficiary

class GetBeneficiariesUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<List<Beneficiary>, None>() {
    override suspend fun FlowCollector<ResultState<List<Beneficiary>>>.run(params: None) {
        emit(repository.getBeneficiaries())
    }
}

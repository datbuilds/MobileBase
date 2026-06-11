package com.mobile.base.core.core.domain.usecases.beneficiary

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.data.entities.beneficiary.Bank

import kotlinx.coroutines.flow.FlowCollector

class GetBanksUseCase(
    private val repository: RepositoryBeneficiary
) : BaseUseCase<List<Bank>, None>() {
    override suspend fun FlowCollector<ResultState<List<Bank>>>.run(params: None) {
        emit(repository.getBanks())
    }
}

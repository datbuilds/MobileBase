package com.mobile.base.core.core.domain.usecases.beneficiary

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.ValidateAccountRequest
import com.mobile.base.core.core.domain.source.response.ValidateAccountResponse
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.domain.usecases.BaseUseCase

class ValidateAccountUseCase(private val repository: RepositoryBeneficiary) :
    BaseUseCase<ValidateAccountResponse, ValidateAccountRequest>() {
    override suspend fun FlowCollector<ResultState<ValidateAccountResponse>>.run(params: ValidateAccountRequest) {
        emit(repository.validateAccount(params))
    }
}

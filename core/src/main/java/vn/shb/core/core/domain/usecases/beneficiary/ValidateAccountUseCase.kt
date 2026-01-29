package vn.shb.core.core.domain.usecases.beneficiary

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.ValidateAccountRequest
import vn.shb.core.core.domain.source.response.ValidateAccountResponse
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.domain.usecases.BaseUseCase

class ValidateAccountUseCase(private val repository: RepositoryBeneficiary) :
    BaseUseCase<ValidateAccountResponse, ValidateAccountRequest>() {
    override suspend fun FlowCollector<ResultSHB<ValidateAccountResponse>>.run(params: ValidateAccountRequest) {
        emit(repository.validateAccount(params))
    }
}

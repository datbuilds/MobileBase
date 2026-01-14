package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.domain.source.service.ServiceBeneficiary
import vn.shb.core.core.domain.usecases.beneficiary.RepositoryBeneficiary
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.data.entities.beneficiary.Beneficiary

class RepositoryBeneficiaryImpl(
    private val service: ServiceBeneficiary
) : RepositoryBeneficiary {

    override suspend fun getBeneficiaries(): ResultSHB<List<Beneficiary>> {
        return handleResponse(service.getBeneficiaries()) { response ->
            response.data?.array ?: emptyList()
        }
    }

    override suspend fun getBanks(): ResultSHB<List<Bank>> {
        return handleResponse(service.getBanks()) { response ->
            response.data?.array ?: emptyList()
        }
    }

    override suspend fun deleteBeneficiary(id: String): ResultSHB<ActionDone> {
        return handleResponse(service.deleteBeneficiary(id)) {
            ActionDone
        }
    }

    override suspend fun createBeneficiary(request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<BeneficiaryResponse> {
        return handleResponse(service.createBeneficiary(request)) { it }
    }

    override suspend fun updateBeneficiary(
        id: String, request: BeneficiaryRequest
    ): ResultSHB<BeneficiaryResponse> {
        return handleResponse(service.updateBeneficiary(id, request)) { it }
    }

    private fun <T : BaseResponse<*>, R> handleResponse(
        result: ResultSHB<T>,
        mapSuccess: (T) -> R
    ): ResultSHB<R> {
        return when (result) {
            is ResultSHB.Success -> {
                val response = result.successData
                if (response.isSuccess()) {
                    ResultSHB.Success(mapSuccess(response))
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = response.errorMessage,
                            code = response.errorCode
                        )
                    )
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }
}

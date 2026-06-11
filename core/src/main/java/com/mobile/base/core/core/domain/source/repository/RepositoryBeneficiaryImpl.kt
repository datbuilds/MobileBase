package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.request.RemoveBeneficiaryRequest
import com.mobile.base.core.core.domain.source.response.BeneficiaryResponse
import com.mobile.base.core.core.domain.source.service.ServiceBeneficiary
import com.mobile.base.core.core.domain.usecases.beneficiary.RepositoryBeneficiary
import com.mobile.base.data.entities.beneficiary.Bank
import com.mobile.base.data.entities.beneficiary.Beneficiary

class RepositoryBeneficiaryImpl(
    private val service: ServiceBeneficiary
) : RepositoryBeneficiary {

    override suspend fun getBeneficiaries(): ResultState<List<Beneficiary>> {
        return handleResponse(service.getBeneficiaries()) { response ->
            response.data?.array ?: emptyList()
        }
    }

    override suspend fun getBanks(): ResultState<List<Bank>> {
        return handleResponse(service.getBanks()) { response ->
            response.data?.array ?: emptyList()
        }
    }

    override suspend fun deleteBeneficiary(request: RemoveBeneficiaryRequest): ResultState<ActionDone> {
        return handleResponse(service.deleteBeneficiary(request)) {
            ActionDone
        }
    }

    override suspend fun createBeneficiary(request: com.mobile.base.core.core.domain.source.request.BeneficiaryRequest): ResultState<BeneficiaryResponse> {
        return handleResponse(service.createBeneficiary(request)) { it }
    }

    override suspend fun updateBeneficiary(request: BeneficiaryRequest
    ): ResultState<BeneficiaryResponse> {
        return handleResponse(service.updateBeneficiary( request)) { it }
    }

    override suspend fun validateAccount(request: com.mobile.base.core.core.domain.source.request.ValidateAccountRequest): ResultState<com.mobile.base.core.core.domain.source.response.ValidateAccountResponse> {
        return handleResponse(service.validateAccount(request)) { it }
    }

    private fun <T : BaseResponse<*>, R> handleResponse(
        result: ResultState<T>,
        mapSuccess: (T) -> R
    ): ResultState<R> {
        return when (result) {
            is ResultState.Success -> {
                val response = result.successData
                if (response.isSuccess()) {
                    ResultState.Success(mapSuccess(response))
                } else {
                    ResultState.Failure(
                        AppReason(
                            message = response.errorMessage,
                            code = response.errorCode
                        )
                    )
                }
            }

            is ResultState.Failure -> {
                ResultState.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultState.Loading
            }
        }
    }
}

package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.delivery.toResultDataAction
import vn.shb.core.core.domain.source.response.BeneficiaryResponse
import vn.shb.core.core.domain.source.service.ServiceBeneficiary
import vn.shb.core.core.domain.usecases.beneficiary.RepositoryBeneficiary
import vn.shb.data.entities.beneficiary.Beneficiary

import vn.shb.core.core.domain.source.response.BankResponse
import vn.shb.data.entities.beneficiary.Bank

class RepositoryBeneficiaryImpl(
    private val service: ServiceBeneficiary
) : RepositoryBeneficiary {

    override suspend fun getBeneficiaries(): ResultSHB<List<Beneficiary>> {
        return resultGetBeneficiaries(service.getBeneficiaries())
    }

    override suspend fun getBanks(): ResultSHB<List<Bank>> {
        return resultGetBanks(service.getBanks())
    }

    override suspend fun deleteBeneficiary(id: String): ResultSHB<ActionDone> {
        return service.deleteBeneficiary(id).toResultDataAction()
    }

    override suspend fun createBeneficiary(request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<BeneficiaryResponse> {
        return service.createBeneficiary(request)
    }

    override suspend fun updateBeneficiary(id: String, request: vn.shb.core.core.domain.source.request.BeneficiaryRequest): ResultSHB<BeneficiaryResponse> {
        return service.updateBeneficiary(id, request)
    }

    private fun resultGetBanks(result: ResultSHB<BankResponse>): ResultSHB<List<Bank>> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultSHB.Success(contentResult.data?.array ?: emptyList())
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
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

    private fun resultGetBeneficiaries(result: ResultSHB<BeneficiaryResponse>): ResultSHB<List<Beneficiary>> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    ResultSHB.Success(contentResult.data?.array ?: emptyList())
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
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

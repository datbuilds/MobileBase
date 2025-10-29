package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason

abstract class RepositoryBaseImpl() {

    fun handleFailure(reason: Reason): ResultSHB.Failure {
        return returnFailure(reason.errMessage, reason.errorCode)
    }

    fun <T> handleFailure(response: BaseResponse<T>): ResultSHB.Failure {
        return returnFailure(response.errorMessage, response.errorCode)
    }

    private fun returnFailure(message: String, code: String): ResultSHB.Failure {
        return ResultSHB.Failure(
            AppReason(
                message = message,
                code = code
            )
        )
    }
}
package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.delivery.Reason
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.reason.AppReason

abstract class RepositoryBaseImpl() {

    fun handleFailure(reason: Reason): ResultState.Failure {
        return returnFailure(reason.errMessage, reason.errorCode)
    }

    fun <T> handleFailure(response: BaseResponse<T>): ResultState.Failure {
        return returnFailure(response.errorMessage, response.errorCode)
    }

    private fun returnFailure(message: String, code: String): ResultState.Failure {
        return ResultState.Failure(
            AppReason(
                message = message,
                code = code
            )
        )
    }
}
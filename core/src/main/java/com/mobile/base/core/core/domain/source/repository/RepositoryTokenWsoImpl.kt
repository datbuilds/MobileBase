package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.reason.AppReason
import com.mobile.base.core.core.domain.source.service.ServiceWso2
import com.mobile.base.core.core.domain.usecases.wso2.RepositoryWSO
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import com.mobile.base.data.entities.wso2.WsoData

class RepositoryTokenWsoImpl(
    private val serviceWso2: ServiceWso2,
) : RepositoryWSO {

    override suspend fun getToken(
        token: String,
        params: UseCaseGetTokenWso2.Params
    ): ResultState<WsoData> {
        return resultGetTokenWso2(serviceWso2.getTokenWso2(token, params))
    }

    override suspend fun refreshToken(
        token: String,
        params: UseCaseRefreshTokenWso2.Params
    ): ResultState<WsoData> {
        return resultRefreshTokenWso2(serviceWso2.rfTokenWso2(token = token, params))
    }

    private fun resultRefreshTokenWso2(result: ResultState<WsoData>): ResultState<WsoData> {
        return when (result) {
            is ResultState.Success -> {
                ResultState.Success(result.successData)
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

    private fun resultGetTokenWso2(result: ResultState<WsoData>): ResultState<WsoData> {
        return when (result) {
            is ResultState.Success -> {
                ResultState.Success(result.successData)
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
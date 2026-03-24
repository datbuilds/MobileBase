package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.source.service.ServiceWso2
import vn.shb.core.core.domain.usecases.wso2.RepositoryWSO
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import vn.shb.data.entities.wso2.WsoData

class RepositoryTokenWsoImpl(
    private val serviceWso2: ServiceWso2,
) : RepositoryWSO {

    override suspend fun getToken(
        token: String,
        params: UseCaseGetTokenWso2.Params
    ): ResultSHB<WsoData> {
        return resultGetTokenWso2(serviceWso2.getTokenWso2(token, params))
    }

    override suspend fun refreshToken(
        token: String,
        params: UseCaseRefreshTokenWso2.Params
    ): ResultSHB<WsoData> {
        return resultRefreshTokenWso2(serviceWso2.rfTokenWso2(token = token, params))
    }

    private fun resultRefreshTokenWso2(result: ResultSHB<WsoData>): ResultSHB<WsoData> {
        return when (result) {
            is ResultSHB.Success -> {
                ResultSHB.Success(result.successData)
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

    private fun resultGetTokenWso2(result: ResultSHB<WsoData>): ResultSHB<WsoData> {
        return when (result) {
            is ResultSHB.Success -> {
                ResultSHB.Success(result.successData)
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
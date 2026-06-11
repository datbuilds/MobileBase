package com.mobile.base.core.core.domain.usecases.wso2

import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.data.entities.login.UserLog
import com.mobile.base.data.entities.wso2.WsoData

interface RepositoryWSO {

    suspend fun getToken(token: String, params: UseCaseGetTokenWso2.Params): ResultState<WsoData>
    suspend fun refreshToken(token: String, params: UseCaseRefreshTokenWso2.Params): ResultState<WsoData>

}
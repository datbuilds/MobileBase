package com.mobile.core.core.domain.usecases.login

import com.mobile.core.core.delivery.ActionDone
import com.mobile.core.core.delivery.ResultState
import com.mobile.core.core.domain.source.response.SystemVarData
import com.mobile.data.entities.login.RegisterDeviceData
import com.mobile.data.entities.login.RegisterDeviceRequest
import com.mobile.data.entities.login.UserLog

interface RepositoryAuth {

    suspend fun login(params: UseCaseLogin.Params): ResultState<UserLog>

    suspend fun logout(): ResultState<ActionDone>

    suspend fun refreshToken(params: UseCaseRefreshToken.Params): ResultState<UserLog>

    suspend fun getSystemVars(name: String): ResultState<SystemVarData>

    suspend fun registerDevice(headers: Map<String, String>, params: RegisterDeviceRequest): ResultState<RegisterDeviceData>

    suspend fun verifyDevice(headers: Map<String, String>, params: com.mobile.data.entities.login.VerifyDeviceRequest): ResultState<UserLog>
}
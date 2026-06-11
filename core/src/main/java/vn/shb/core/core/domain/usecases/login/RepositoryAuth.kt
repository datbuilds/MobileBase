package vn.shb.core.core.domain.usecases.login

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.SystemVarData
import vn.shb.data.entities.login.RegisterDeviceData
import vn.shb.data.entities.login.RegisterDeviceRequest
import vn.shb.data.entities.login.UserLog

interface RepositoryAuth {

    suspend fun login(params: UseCaseLogin.Params): ResultSHB<UserLog>

    suspend fun logout(): ResultSHB<ActionDone>

    suspend fun refreshToken(params: UseCaseRefreshToken.Params): ResultSHB<UserLog>

    suspend fun getSystemVars(name: String): ResultSHB<SystemVarData>

    suspend fun registerDevice(headers: Map<String, String>, params: RegisterDeviceRequest): ResultSHB<RegisterDeviceData>

    suspend fun verifyDevice(headers: Map<String, String>, params: vn.shb.data.entities.login.VerifyDeviceRequest): ResultSHB<UserLog>
}
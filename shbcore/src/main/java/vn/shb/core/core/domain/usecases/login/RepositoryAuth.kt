package vn.shb.core.core.domain.usecases.login

import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.data.entities.login.UserInfo

interface RepositoryAuth {

    suspend fun login(params: UseCaseLogin.Params): ResultSHB<StateLogin>

    suspend fun logout(params: UseCaseLogout.Params): ResultSHB<ActionDone>

    suspend fun refreshToken(params: UseCaseRefreshToken.Params): ResultSHB<UserInfo>
}
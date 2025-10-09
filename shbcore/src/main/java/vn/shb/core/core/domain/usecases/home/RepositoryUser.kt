package vn.shb.core.core.domain.usecases.home

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.data.entities.login.UserInfo

interface RepositoryUser {
    suspend fun getUserInfo(): ResultSHB<UserInfo>
}
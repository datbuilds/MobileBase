package vn.shb.core.core.domain.usecases.home

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.UserInfo

interface RepositoryUser {
    suspend fun getUserInfo(): ResultSHB<UserInfo>

    suspend fun getAccountsInfos() : ResultSHB<AccountData>
}
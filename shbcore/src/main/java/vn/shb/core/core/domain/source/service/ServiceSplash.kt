package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.domain.source.api.ApiSplash
import vn.shb.core.core.retrofit.SafeExecute

class ServiceSplash(private val api: ApiSplash) : SafeExecute() {
    suspend fun checkVersion(currentVersion: String) = execute {
        api.checkVersion(version = currentVersion).awaitResponse()
    }
}
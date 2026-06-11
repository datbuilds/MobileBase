package com.mobile.base.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.base.core.core.domain.source.api.ApiSplash
import com.mobile.base.core.core.retrofit.SafeExecute

class ServiceSplash(private val api: ApiSplash) : SafeExecute() {
    suspend fun checkVersion(currentVersion: String) = execute {
        api.checkVersion(version = currentVersion).awaitResponse()
    }
}
package com.mobile.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.core.core.domain.source.api.ApiSplash
import com.mobile.core.core.retrofit.SafeExecute

class ServiceSplash(private val api: ApiSplash) : SafeExecute() {
    suspend fun checkVersion(currentVersion: String) = execute {
        api.checkVersion(version = currentVersion).awaitResponse()
    }
}
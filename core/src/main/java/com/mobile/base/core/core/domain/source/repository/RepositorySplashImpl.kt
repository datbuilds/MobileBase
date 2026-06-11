package com.mobile.base.core.core.domain.source.repository

import com.mobile.base.core.core.domain.source.service.ServiceSplash
import com.mobile.base.core.core.domain.usecases.splash.RepositorySplash
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage

class RepositorySplashImpl(
    private val currentVersion: String,
    private val storage: AndroidSecureStorage,
    private val service: ServiceSplash,
) : RepositorySplash {
}
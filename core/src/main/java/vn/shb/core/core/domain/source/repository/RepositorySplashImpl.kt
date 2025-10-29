package vn.shb.core.core.domain.source.repository

import vn.shb.core.core.domain.source.service.ServiceSplash
import vn.shb.core.core.domain.usecases.splash.RepositorySplash
import vn.shb.core.core.security.encrypt.AndroidSecureStorage

class RepositorySplashImpl(
    private val currentVersion: String,
    private val storage: AndroidSecureStorage,
    private val service: ServiceSplash,
) : RepositorySplash {
}
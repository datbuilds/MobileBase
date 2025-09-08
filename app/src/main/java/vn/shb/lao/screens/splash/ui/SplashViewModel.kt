package vn.shb.lao.screens.splash.ui

import androidx.lifecycle.ViewModel
import vn.shb.core.core.security.encrypt.AndroidSecureStorage

class SplashViewModel(
    private val storage: AndroidSecureStorage,
) : ViewModel() {}
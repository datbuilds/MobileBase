package vn.shb.cam.screens.splash.ui

import androidx.lifecycle.ViewModel
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.cam.base.BaseViewModel

class SplashViewModel(
    private val storage: AndroidSecureStorage,
) : BaseViewModel() {}
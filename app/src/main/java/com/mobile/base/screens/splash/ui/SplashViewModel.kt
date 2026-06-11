package com.mobile.base.screens.splash.ui

import androidx.lifecycle.ViewModel
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.base.BaseViewModel

class SplashViewModel(
    private val storage: AndroidSecureStorage,
) : BaseViewModel() {}
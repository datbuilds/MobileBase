package com.mobile.screens.splash.ui

import androidx.lifecycle.ViewModel
import com.mobile.core.core.security.encrypt.AndroidSecureStorage
import com.mobile.base.BaseViewModel

class SplashViewModel(
    private val storage: AndroidSecureStorage,
) : BaseViewModel() {}
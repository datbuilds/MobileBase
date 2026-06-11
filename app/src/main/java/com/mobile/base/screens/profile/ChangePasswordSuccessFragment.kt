package com.mobile.base.screens.profile

import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentChangePasswordSuccessBinding
import com.mobile.base.utils.extensions.returnActivity
import com.mobile.base.core.utils.extesions.setOnSingleClickListener

class ChangePasswordSuccessFragment :
    BaseFragmentBinding<FragmentChangePasswordSuccessBinding>(FragmentChangePasswordSuccessBinding::inflate) {

    override fun initView(view: View) {
        // No specific initialization needed for static UI
    }

    override fun initListener() {
        binding.btnLogin.setOnSingleClickListener {
            logoutAndNavigateToLogin()
        }
    }

    override fun initObserve() {
        // No observation needed
    }

    private fun logoutAndNavigateToLogin() {
        lifecycleScope.launch {
            launch {
                logout()
            }
        }
    }
}

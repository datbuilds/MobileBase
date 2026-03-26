package vn.shb.cam.screens.profile

import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.cam.activity.MainActivity
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentChangePasswordSuccessBinding
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.returnActivity
import vn.shb.cam.utils.refreshTK.RefreshTokenManager

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
                RefreshTokenManager.stop()
                storage.resetToken()
                delay(260) // Optional delay for smooth transition if needed, kept from ProfileFragment logic
                requireActivity().apply {
                    finishAffinity()
                    returnActivity(MainActivity.loginIntent(requireContext()))
                }
            }
        }
    }
}

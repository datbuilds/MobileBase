package vn.shb.lao.screens.profile

import android.view.View
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentChangePasswordSuccessBinding
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.refreshTK.RefreshTokenManager

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
        launchRepeatOnLifecycle {
            launch {
                RefreshTokenManager.stop()
                storage.resetToken()
                delay(260) // Optional delay for smooth transition if needed, kept from ProfileFragment logic
                requireActivity().apply {
                    finishAffinity()
                    returnActivity(LoginActivity.intent(requireContext()))
                }
            }
        }
    }
}

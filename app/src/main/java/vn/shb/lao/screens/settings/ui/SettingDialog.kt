package vn.shb.lao.screens.settings.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.login.UserConverters.stringToUserInfo
import vn.shb.lao.BuildConfig
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseDialogBinding
import vn.shb.lao.databinding.DialogSettingBinding
import vn.shb.lao.utils.extensions.loadAvatarText
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.extensions.visibleWhenTrue
import vn.shb.lao.utils.refreshTK.RefreshTokenManager
import vn.shb.lao.utils.view.actionView.OnToolbarListener

class SettingDialog : BaseDialogBinding<DialogSettingBinding>(DialogSettingBinding::inflate) {

    companion object {
        const val TAG = "SettingDialogFragment"
    }

    class Build() {
        fun build() = SettingDialog()
    }

    private val storage: AndroidSecureStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setWindowAnimations(R.style.DialogAnimation)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun initView(view: View) {
        with(binding) {
            toolbar.setTitle("Cài đặt")
            stringToUserInfo(storage.getUserLog())?.let { user ->
                ivAvatar.loadAvatarText(
                    fallbackName = user.username,
                    colorBg = "#FFFFFF",
                    colorStoke = "#FC7513",
                    colorText = "#FC7513"
                )
                tvUserName.text = user.username
                tvTitleUser.text = user.title
                tvTitleUser.visibleWhenTrue(user.title.isNotEmpty())
            }
            tvVersion.text = "Phiên bản ${BuildConfig.VERSION_NAME}"
        }
    }

    override fun initListener() {
        with(binding) {
            toolbar.setListener(object : OnToolbarListener {
                override fun onPreviousClick() {
                    dismissAllowingStateLoss()
                }
            })

            swBiometric.setOnCheckedChangeListener { buttonView, checked ->
                if (checked) {
                    showDialogError(message = "Tính năng này hiện đang được hoàn thiện và sẽ sớm ra mắt trong thời gian tới!") {
                        buttonView.setChecked(false)
                    }
                }
            }

            btLogout.setOnSingleClickListener {
                openLogoutDialog()
            }
        }
    }

    private fun openLogoutDialog() {
        val dialog = BSLogoutDialog.Build {
            clearPref()
        }.build()
        dialog.show(childFragmentManager, BSLogoutDialog.TAG)
    }

    private fun clearPref() {
        lifecycleScope.launch {
            RefreshTokenManager.stop()
            delay(260)
            storage.resetToken()
            requireActivity().apply {
                finishAffinity()
                returnActivity(LoginActivity.intent(requireContext()))
            }
        }
    }

    override fun initObserve() {
    }
}

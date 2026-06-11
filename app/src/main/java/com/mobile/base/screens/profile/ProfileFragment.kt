package com.mobile.base.screens.profile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.home.UserInfo
import com.mobile.base.choosephotohelper.ChoosePhotoHelper
import com.mobile.base.choosephotohelper.callback.ChoosePhotoCallback
import com.mobile.base.BuildConfig
import com.mobile.base.R
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.base.view.MyTextView
import com.mobile.base.databinding.FragmentProfileBinding
import com.mobile.base.databinding.ItemProfileInfoBinding
import com.mobile.base.databinding.LayoutLanguagePopupBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.screens.home.getTypeAccount
import com.mobile.base.screens.home.widget.OnClickDetail
import com.mobile.base.screens.login.state.LogoutUiState
import com.mobile.base.screens.login.ui.LoginViewModel
import com.mobile.base.screens.login.ui.widget.setDisableAlpha
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.hideProgressDialog
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.extensions.returnActivity
import com.mobile.base.utils.extensions.showProgressDialog
import com.mobile.base.utils.view.dialog.BottomSheetDialogHelper
import com.mobile.base.utils.widgets.LocaleHelper
import com.mobile.base.core.core.delivery.ReasonDescription.CAM
import com.mobile.base.core.core.delivery.ReasonDescription.ENGLISH
import com.mobile.base.core.core.delivery.ReasonDescription.VIET
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2

class ProfileFragment :
    BaseFragmentBinding<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: LoginViewModel by inject()
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()
    private val useCaseRefreshTokenWso2: UseCaseRefreshTokenWso2 by inject()

    private lateinit var photoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .alwaysShowRemoveOption(true)
            .build(object : ChoosePhotoCallback<String> {
                override fun onChoose(photo: String?) {
                    photo?.let { path -> setPathAvatarUser(path) }
                    getCurrentUser()?.let {
                        binding.flAvatarUser.setUserName(
                            getPathAvatarUser(),
                            it.username
                        )
                    }
                    showAvatarUpdateStatus(binding.tvStatusUpdateAvatar, true)
                }

                override fun onError() {
                    showAvatarUpdateStatus(binding.tvStatusUpdateAvatar, false)
                }
            })
    }

    override fun initView(view: View) {
        val user = homeViewModel.getCurrentUserInfo()
        if (user != null) {
            bindViewDetail(user)
        } else {
            homeViewModel.getUserInfo()
        }

        LocaleHelper.getResourceLocale(LocaleHelper.getCurrentLanguage(requireContext())) { resId ->
            binding.ivLogoLanguage.setImageResource(resId)
        }

        binding.tvHotline.text =
            getString(R.string.version).plus(Const.SEPARATOR_SPACE).plus(BuildConfig.VERSION_NAME)
    }

    private fun getNameAccountDefault(defaultAccount: String?): String {
        if (defaultAccount.isNullOrEmpty()) return ""
        val listAccount = homeViewModel.getListAccount()
        val ac = listAccount.firstOrNull { it.accountNumber == defaultAccount }
        return if (ac != null) {
            getTypeAccount(requireContext(), ac).plus(Const.SEPARATOR_DASH)
                .plus(ac.accountNumber)
        } else {
            ""
        }
    }

    private fun bindViewDetail(user: UserInfo) {

        with(binding) {
            flAvatarUser.setUserName(getPathAvatarUser(), user.customerName)
            tvNameUser.text = user.customerName
            iclInfo1.bind(getString(R.string.customerId), user.customerId)
            iclInfo2.bind(getString(R.string.customerName), user.customerName)
            val defaultAccount = getNameAccountDefault(user.defaultAcct)
            iclInfo3.bind(getString(R.string.defaultCasaAccount), defaultAccount)
            iclInfo4.bind(getString(R.string.email), user.email)
//            iclInfo5.bind(getString(R.string.shbOnline), user.authMethodName)
            iclInfo5.root.gone()
            iclInfo6.bind(getString(R.string.userName), user.username)
        }

    }

    private fun ItemProfileInfoBinding.bind(label: String, value: String) {
        tvLabel.text = label
        tvValue.text = value
    }

    override fun initListener() {
        with(binding) {
            tvMyProfile.setOnSingleClickListener {
                popBackTo(AppDestination.HomeArg())
            }
            tvChangePassword.setOnSingleClickListener {
                safeNavigate(AppDestination.ChangePassword())
            }
            btnLogout.setOnSingleClickListener {
                BottomSheetDialogHelper(requireContext()).message(
                    getString(R.string.notification),
                    getString(R.string.logoutConfirm),
                    textPositive = getString(R.string.logout),
                    textNegative = getString(R.string.cancel),
                    positiveAction = {
                        viewModel.logout()
                    }
                )
            }

            flAvatarUser.setListener(
                object : OnClickDetail {
                    override fun onAvatarClick() {
                        val dialog = DialogChooseProfilePicture.Build { type ->
                            if (type == ChoosePhotoHelper.ActionProfile.CAMERA.toString()) {
                                photoHelper.takePhoto()
                            } else {
                                photoHelper.chooseFromGallery()
                            }
                        }.build()
                        dialog.show(childFragmentManager, DialogChooseProfilePicture.TAG)
                    }
                })

            llLanguage.setOnSingleClickListener {
                showLanguagePopup(llLanguage)
            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                viewModel.stateLogout.collectLatest { state ->
                    if (state is LogoutUiState.Success) {
                        lifecycleScope.launch {
                            delay(260)
                            logout()
                        }
                    }
                    if (state is LogoutUiState.Error) {
                        handleErrorHome(state.reason)
                        hideProgressDialog()
                    }
                    if (state is LogoutUiState.Loading) {
                        showProgressDialog()
                    }
                }
            }
            launch {
                homeViewModel.stateUserInfo.collect { userInfo ->
                    userInfo?.let { bindViewDetail(it) }
                }
            }
//            launch {
//                homeViewModel.stateSelectedAccount.collect { accountInfo ->
//                    homeViewModel.getCurrentUserInfo()?.let { bindViewDetail(it, accountInfo) }
//                }
//            }
        }
    }

    fun showAvatarUpdateStatus(
        textView: MyTextView,
        isSuccess: Boolean
    ) {
        textView.visibility = View.VISIBLE

        if (isSuccess) {
            textView.apply {
                text = context.getString(R.string.profilePictureUpdated)
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_success,
                    0,
                    R.drawable.ic_close,
                    0
                )
                setBackgroundResource(R.drawable.bg_toast_change_avatar_ss)
            }
        } else {
            textView.apply {
                text = context.getString(R.string.profilePictureFailed)
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error,
                    0,
                    R.drawable.ic_close,
                    0
                )
                setBackgroundResource(R.drawable.bg_toast_change_avatar_error)
            }
        }

        textView.animate()
            .alpha(1f)
            .setDuration(200)
            .withEndAction {
                textView.postDelayed({
                    textView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            textView.visibility = View.GONE
                            textView.alpha = 1f
                        }
                        .start()
                }, 3000)
            }
            .start()
    }

    private fun showLanguagePopup(anchor: View) {
        val binding = LayoutLanguagePopupBinding.inflate(LayoutInflater.from(anchor.context))

        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // focusable, click outside sẽ tự đóng
        )

        val currentLanguage = LocaleHelper.getCurrentLanguage(requireContext())

        // style
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 8f

        binding.apply {

            iclLanguage1.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_cam)
                tvNameLanguage.text = getString(R.string.cambodian)
                root.setDisableAlpha(currentLanguage == CAM)
                root.setOnSingleClickListener {
                    updateLanguage(CAM)
                    popupWindow.dismiss()
                }
            }
            iclLanguage2.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_uk)
                tvNameLanguage.text = getString(R.string.english)
                root.setDisableAlpha(currentLanguage == ENGLISH)
                root.setOnSingleClickListener {
                    updateLanguage(ENGLISH)
                    popupWindow.dismiss()
                }
            }

            iclLanguage3.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_vn)
                tvNameLanguage.text = getString(R.string.vietnamese)
                root.setDisableAlpha(currentLanguage == VIET)
                root.setOnSingleClickListener {
                    updateLanguage(VIET)
                    popupWindow.dismiss()
                }
            }
        }

        val marginRight = (130 * anchor.context.resources.displayMetrics.density).toInt()
        popupWindow.showAsDropDown(anchor, -marginRight, 0, Gravity.END)
    }

    fun updateLanguage(type: String) {
        context?.let { ct ->
            LocaleHelper.saveLanguage(ct, type)
            storage.setLanguage(type)
            LocaleHelper.setLocale(ct, type)
//            restartApp(activity!!)
            requireActivity().recreate()
        }
    }
}

package vn.shb.lao.screens.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.home.UserInfo
import vn.shb.dn.choosePhotoHelper.ChoosePhotoHelper
import vn.shb.dn.choosePhotoHelper.callback.ChoosePhotoCallback
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.FragmentProfileBinding
import vn.shb.lao.databinding.ItemProfileInfoBinding

import vn.shb.lao.screens.home.getTypeAccount
import vn.shb.lao.screens.home.widget.OnClickDetail
import vn.shb.lao.screens.login.state.LogoutUiState
import vn.shb.lao.screens.login.ui.LoginViewModel
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.refreshTK.RefreshTokenManager
import vn.shb.lao.utils.refreshTK.RefreshTokenWso2Manager
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

class ProfileFragment :
    BaseFragmentBinding<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: LoginViewModel by inject()


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
        val account = homeViewModel.selectedAccount
        if (user != null && account != null) {
            bindViewDetail(user, account)
        } else {
            homeViewModel.getUserInfo()
            Log.i("2332323", "get API")
        }
    }

    private fun bindViewDetail(user: UserInfo, account: AccountBase) {

        with(binding) {
            flAvatarUser.setUserName(getPathAvatarUser(), user.customerName)
            tvNameUser.text = user.customerName

            iclInfo1.bind(getString(R.string.customerId), user.customerId)
            iclInfo2.bind(getString(R.string.customerName), user.customerName)
            val defaultAccount =
                homeViewModel.selectedAccount?.let {
                    getTypeAccount(
                        requireContext(),
                        it
                    )
                }.plus(
                    Const.SEPARATOR_DASH
                )
                    .plus(account.accountNumber)
            iclInfo3.bind(getString(R.string.defaultCasaAccount), defaultAccount)
            iclInfo4.bind(getString(R.string.email), user.email)
            iclInfo5.bind(getString(R.string.shbOnline), user.authMethodName)
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
                safeNavigate(R.id.profileFragment, R.id.homeFragment)
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
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                viewModel.stateLogout.collectLatest { state ->
                    if (state is LogoutUiState.Success) {
                        lifecycleScope.launch {
                            RefreshTokenManager.stop()
                            RefreshTokenWso2Manager.stop()
                            delay(260)
                            storage.resetToken()
                            requireActivity().apply {
                                finishAffinity()
                                returnActivity(LoginActivity.intent(requireContext()))
                            }
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
                    val userLog = getCurrentUser()
                    userLog?.let {
                        homeViewModel.selectedAccount?.let { bindViewDetail(userInfo, it) }
                    }
                }
            }
            launch {
                homeViewModel.stateSelectedAccount.collect { accountInfo ->
                    homeViewModel.getCurrentUserInfo()?.let { bindViewDetail(it, accountInfo) }
                }
            }
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
}
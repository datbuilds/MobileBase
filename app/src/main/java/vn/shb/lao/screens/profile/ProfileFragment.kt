package vn.shb.lao.screens.profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.dn.choosePhotoHelper.ChoosePhotoHelper
import vn.shb.dn.choosePhotoHelper.callback.ChoosePhotoCallback
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentProfileBinding
import vn.shb.lao.databinding.ItemProfileInfoBinding
import vn.shb.lao.screens.home.HomeViewModel
import vn.shb.lao.screens.home.widget.OnClickDetail
import vn.shb.lao.screens.login.state.LogoutUiState
import vn.shb.lao.screens.login.ui.LoginViewModel
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.extensions.showProgressDialog
import vn.shb.lao.utils.refreshTK.RefreshTokenManager
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

class ProfileFragment :
    BaseFragmentBinding<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: LoginViewModel by inject()

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var photoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .alwaysShowRemoveOption(true)
            .build(object : ChoosePhotoCallback<String> {
                override fun onChoose(photo: String?) {
                    val currentUser =
                        getCurrentUser()?.apply {
                            pathAvatarUser = photo ?: ""
                        }
                    currentUser?.toUserString()?.let {
                        storage.setUserLog(it)
                        binding.flAvatarUser.setUserName(
                            currentUser.pathAvatarUser,
                            currentUser.username
                        )
                    }
                }
            })
    }

    override fun initView(view: View) {
        bindViewDetail()
    }

    private fun bindViewDetail() {
        val user = homeViewModel.getCurrentUserInfo()
        user?.let {
            with(binding) {
                flAvatarUser.setUserName(getCurrentUser()?.pathAvatarUser?:"", it.customerName)
                tvNameUser.text = it.username

                iclInfo1.bind(getString(R.string.customerID), it.customerId)
                iclInfo2.bind(getString(R.string.customerName), it.customerName)
                iclInfo3.bind(getString(R.string.defaultCasaAccount), it.defaultAcct?:"")
                iclInfo4.bind(getString(R.string.email), it.email)
                iclInfo5.bind(getString(R.string.shbOnline), it.authMethodName)
                iclInfo6.bind(getString(R.string.userName), it.username)
            }
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
                BottomSheetDialogHelper(context!!).message(
                    getString(R.string.notificationLabel),
                    getString(R.string.areYouSureWantToLogout),
                    textPositive = getString(R.string.logoutLabel),
                    textNegative = getString(R.string.cancelLabel),
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
                            delay(260)
                            storage.resetToken()
                            requireActivity().apply {
                                finishAffinity()
                                returnActivity(LoginActivity.intent(requireContext()))
                            }
                        }
                    }
                    if (state is LogoutUiState.Error) {
                        showDialogError(state.reason)
                        hideProgressDialog()
                    }
                    if (state is LogoutUiState.Loading) {
                        showProgressDialog()
                    }
                }
            }
        }
    }

}
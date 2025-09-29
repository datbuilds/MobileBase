package vn.shb.lao.screens.profile

import android.os.Bundle
import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.login.UserConverters
import vn.shb.dn.choosePhotoHelper.ChoosePhotoHelper
import vn.shb.dn.choosePhotoHelper.callback.ChoosePhotoCallback
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentProfileBinding
import vn.shb.lao.databinding.ItemProfileInfoBinding
import vn.shb.lao.screens.home.widget.OnClickDetail

class ProfileFragment :
    BaseFragmentBinding<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

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
                        storage.setUserInfo(it)
                        binding.flAvatarUser.setUserName(currentUser.pathAvatarUser, currentUser.username)
                    }
                }
            })
    }

    override fun initView(view: View) {
        bindViewDetail()
    }

    private fun bindViewDetail() {
        val user = getCurrentUser()
        user?.let {
            with(binding) {
                flAvatarUser.setUserName(it.pathAvatarUser, it.username)
                tvNameUser.text = it.username

                iclInfo1.bind(getString(R.string.customerID), it.id_token)
                iclInfo2.bind(getString(R.string.customerName), it.username)
                iclInfo3.bind(getString(R.string.defaultCasaAccount), it.title)
                iclInfo4.bind(getString(R.string.email), it.userLog)
                iclInfo5.bind(getString(R.string.shbOnline), it.userLog)
                iclInfo6.bind(getString(R.string.userName), it.id_token)
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

            }
            btnLogout.setOnSingleClickListener {
//                storage.clearUserInfo()
//                navController.navigate(R.id.action_profileFragment_to_loginFragment)
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
    }

}
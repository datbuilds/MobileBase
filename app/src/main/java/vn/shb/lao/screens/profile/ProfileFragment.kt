package vn.shb.lao.screens.profile

import android.view.View
import vn.shb.data.entities.login.UserConverters
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentProfileBinding

class ProfileFragment :
    BaseFragmentBinding<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun initView(view: View) {
        bindViewDetail()
    }

    private fun bindViewDetail() {
        val user = UserConverters.stringToUserInfo(storage.getUserInfo())
        user?.let {
            with(binding) {
                flAvatarUser.setUserName("", it.username)
                tvNameUser.text = it.username

                iclInfo1.apply {
                    tvLabel.text = getString(R.string.customerID)
                    tvValue.text = it.id_token
                }
                iclInfo2.apply {
                    tvLabel.text = getString(R.string.customerName)
                    tvValue.text = it.username
                }
                iclInfo3.apply {
                    tvLabel.text = getString(R.string.defaultCasaAccount)
                    tvValue.text = it.title
                }
                iclInfo4.apply {
                    tvLabel.text = getString(R.string.email)
                    tvValue.text = it.userLog
                }
                iclInfo5.apply {
                    tvLabel.text = getString(R.string.shbOnline)
                    tvValue.text = it.userLog
                }
                iclInfo6.apply {
                    tvLabel.text = getString(R.string.userName)
                    tvValue.text = it.id_token
                }
            }
        }

    }

    override fun initListener() {
    }

    override fun initObserve() {
    }

}
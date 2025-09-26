package vn.shb.lao.screens.home

import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getInitials
import vn.shb.data.entities.login.UserConverters
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentHomeBinding
import vn.shb.lao.utils.extensions.getTextWelcomeUser

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    companion object {
    }

    override fun initView(view: View) {
        bindView()
        mapUserInfo()
    }

    private fun bindView() {
        binding.apply {
            tvHelloUser.text = context!!.getTextWelcomeUser()
            incItemTransfer.apply {
                ivIconFeature.setImageResource(R.drawable.ic_transfer)
                tvTitleFeature.text = getString(R.string.transfer)
            }
            incItemAccounts.apply {
                ivIconFeature.setImageResource(R.drawable.ic_accounts)
                tvTitleFeature.text = getString(R.string.accounts)
            }
        }


    }

    private fun mapUserInfo() {
        UserConverters.stringToUserInfo(storage.getUserInfo())?.let { user ->
            binding.tvNameUser.text = user.username
            binding.flAvatarUser.setUserName("", user.username.getInitials())
        }
    }

    override fun initListener() {
        with(binding) {
            flAvatarUser.setOnSingleClickListener {

            }

            incItemTransfer.root.setOnSingleClickListener {

            }
            incItemAccounts.root.setOnSingleClickListener {

            }
        }
    }

    override fun initObserve() {}
}

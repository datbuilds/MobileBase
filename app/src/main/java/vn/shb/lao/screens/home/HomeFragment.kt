package vn.shb.lao.screens.home

import android.view.View
import org.koin.android.ext.android.inject
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserConverters.stringToUserInfo
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentHomeBinding
import vn.shb.lao.screens.home.widget.OnWelcomeListener
import vn.shb.lao.screens.settings.ui.SettingDialog

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val storage: AndroidSecureStorage by inject()

    private fun comingSoon() {
        showDialogError(
            title = "Thông báo",
            message = "Tính năng này hiện đang được hoàn thiện và sẽ sớm ra mắt trong thời gian tới!",
        )
    }

    override fun initView(view: View) {
        mapUserInfo()
    }

    private fun mapUserInfo() {
        stringToUserInfo(storage.getUserInfo())?.let { user ->
            binding.welcomeView.setUserName(user.username)
        }
    }

    // back ve vi tri dau tien
//    private fun scrollToNextBanner() {
//        with(binding.rvBannerTop) {
//            val layoutManager = bannerLayoutManager
//            val bannerSize = bannersTop?.size ?: return
//            if (bannerSize == 0) return
//
//            val view = snapHelper.findSnapView(layoutManager)
//            val currentPosition = if (view != null) {
//                layoutManager.getPosition(view)
//            } else {
//                RecyclerView.NO_POSITION
//            }
//
//            currentIndex = (currentPosition + 1) % bannerSize
//            smoothScrollToPosition(currentIndex)
//        }
//    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun initListener() {
        with(binding) {
            welcomeView.setListener(object : OnWelcomeListener {
                override fun onAvatarClick() {
                    showSettingDialog()
                }

                override fun onNotificationClick() {
                    comingSoon()
                }

                private fun showSettingDialog() {
                    val dialog = SettingDialog.Build().build()
                    if (isDialogShowing(SettingDialog.TAG)) {
                        return
                    }
                    dialog.show(childFragmentManager, SettingDialog.TAG)
                }
            })
        }
    }

    override fun initObserve() {}
}

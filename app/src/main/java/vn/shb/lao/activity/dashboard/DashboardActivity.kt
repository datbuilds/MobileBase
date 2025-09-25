package vn.shb.lao.activity.dashboard

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import vn.shb.lao.R
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ActivityDashboardBinding
import vn.shb.lao.screens.four.FourFragment
import vn.shb.lao.screens.two.TwoFragment
import vn.shb.lao.screens.three.ThreeFragment
import vn.shb.lao.screens.home.HomeFragment
import vn.shb.lao.utils.extensions.disableOverScrollMode
import vn.shb.lao.utils.extensions.fadeAndScaleToItem

class DashboardActivity :
    BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {

    companion object {
        @JvmStatic
        fun intent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }

    override fun shouldStartRefreshTokenManager(): Boolean {
        return true // Start RefreshTokenManager vì user đã login
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun initObserve() {}

}

package vn.shb.lao.activity.dashboard

import android.content.Context
import android.content.Intent
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.databinding.ActivityDashboardBinding
import vn.shb.lao.utils.widgets.LocaleHelper

class DashboardActivity :
    BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.getLanguageContext(it) })
    }
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

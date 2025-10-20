package vn.shb.lao.activity.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.databinding.ActivityDashboardBinding
import vn.shb.lao.utils.widgets.LocaleHelper

class DashboardActivity :
    BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {
    private val sessionTimeout: Long = 15 * 60 * 1000L // 15 phút = 900.000 ms
    private val handler = Handler(Looper.getMainLooper())
    private val logoutRunnable = Runnable {
        showDialogSessionExpire()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.getLanguageContext(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startSessionTimer()
    }

    private fun startSessionTimer() {
        handler.postDelayed(logoutRunnable, sessionTimeout)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(logoutRunnable)
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

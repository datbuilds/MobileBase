package vn.shb.lao.activity.login

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kongqw.network.monitor.NetworkMonitorManager
import com.kongqw.network.monitor.enums.NetworkState
import com.kongqw.network.monitor.interfaces.NetworkMonitor
import org.koin.android.ext.android.inject
import timber.log.Timber
import vn.shb.core.core.bus.EventBus
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.core.utils.logE
import vn.shb.lao.R
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.databinding.ActivityLoginBinding
import vn.shb.lao.utils.extensions.CustomToastShowOnTop
import vn.shb.lao.utils.widgets.LocaleHelper

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private val storage: AndroidSecureStorage by inject()

    private var navController: NavController? = null
    private var token = ""

    companion object {
        @JvmStatic
        fun intent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return intent
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.getLanguageContext(it) })
    }

    override fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        token = storage.getToken()

        val deviceToken = storage.getFcmToken()
        logE("deviceToken ===>", deviceToken)
    }

    override fun initListener() {

    }

    override fun initObserve() {

    }

    override fun onResume() {
        super.onResume()
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardLocked) {

        }
    }

    @NetworkMonitor
    @Suppress("UNUSED")
    fun onNetWorkStateChange(networkState: NetworkState) {
        Timber.e("onNetWorkStateChange  networkState = $networkState")
        val duration = storage.getExpireTime() * 1000L
        val toast = CustomToastShowOnTop(
            this,
            binding.containerLogin,
            icon = R.drawable.ic_warning,
            message = getString(vn.shb.lao.localization.R.string.notification_network_not_available),
            background = R.drawable.bg_custom_warning,
            duration = duration
        )
        when (networkState) {
            NetworkState.NONE -> toast.show()

            NetworkState.WIFI -> toast.dismiss()

            else -> toast.dismiss()
        }
    }

    override fun onStart() {
        Timber.d("onStart")
        NetworkMonitorManager.getInstance().register(this)
        super.onStart()
    }

    override fun onStop() {
        Timber.d("onStop")
        NetworkMonitorManager.getInstance().unregister(this)
        EventBus.unregister(this.javaClass.simpleName)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
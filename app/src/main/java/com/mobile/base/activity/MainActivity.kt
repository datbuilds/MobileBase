package com.mobile.base.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.mobile.base.base.BaseActivity
import com.mobile.base.databinding.ActivityMainBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.navigation.AppNavigationTransition
import com.mobile.base.navigation.AppNavigator
import com.mobile.base.navigation.Navigator
import com.mobile.base.navigation.NavigatorHost
import com.mobile.base.screens.home.HomeViewModel
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.widgets.LocaleHelper

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    NavigatorHost {

    private val homeViewModel: HomeViewModel by viewModel()

    override val navigator: Navigator by lazy {
        AppNavigator(
            fragmentManager = supportFragmentManager,
            containerId = binding.mainFragmentContainer.id,
        )
    }

    private val sessionTimeout: Long = 15 * 60 * 1000L // 15 phút = 900.000 ms
    private val handler = Handler(Looper.getMainLooper())

    private val logoutRunnable = Runnable {
        logout(true)
    }

    fun startSessionTimer() {
        handler.postDelayed(logoutRunnable, sessionTimeout)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbackTimeout()
    }

    fun removeCallbackTimeout(){
        handler.removeCallbacks(logoutRunnable)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.getLanguageContext(it) })
    }

    override fun initView() {
        binding.flProcessBar.isVisible = false
        binding.flProcessBar.setOnClickListener { }

        if (supportFragmentManager.findFragmentById(binding.mainFragmentContainer.id) == null) {
            openStartDestination(intent)
        }

        if (resolveStartDestination(intent) is AppDestination.HomeArg) {
            startSmsListener()
        }
    }

    override fun initListener() = Unit

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                homeViewModel.stateLoading.collectLatest { isLoading ->
                    binding.flProcessBar.isVisible = isLoading
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
        openStartDestination(intent)
        if (resolveStartDestination(intent) is AppDestination.HomeArg) {
            startSmsListener()
        }
    }

    override fun dismissTransientUi() {
        binding.flProcessBar.isVisible = false
        homeViewModel.resetLoadingState()
        super.dismissTransientUi()
    }

    override fun shouldStartRefreshTokenManager(): Boolean {
        val startDestination = resolveStartDestination(intent)
        return startDestination is AppDestination.HomeArg || startDestination is AppDestination.Login
    }

    override fun shouldUseDoubleBackToExit(): Boolean {
        return supportFragmentManager.backStackEntryCount == 0 &&
                activeDestination() is AppDestination.HomeArg
    }

    override fun shouldExitAppOnBackImmediately(): Boolean {
        return supportFragmentManager.backStackEntryCount == 0 &&
                activeDestination() is AppDestination.Login
    }

    override fun shouldHandleInactivityTimer(): Boolean {
        return activeDestination()?.let { destination ->
            destination != AppDestination.Splash
        } == true
    }

    override fun isSessionExpiryExempt(): Boolean {
        return activeDestination()?.let { destination ->
            destination == AppDestination.Splash || destination is AppDestination.Login
        } != false
    }

    override fun buildLoginIntent(isShowSessionExpired: Boolean): Intent {
        return loginIntent(this, isShowSessionExpired)
    }

    override fun onBackPressedAction() {
        if (!navigator.goBack()) {
            finish()
        }
    }

    fun onAuthenticatedFlowStarted() {
        setIntent(
            Intent(intent).apply {
                putExtra(EXTRA_START_DESTINATION, START_HOME)
                removeExtra(EXTRA_SHOW_SESSION_EXPIRED)
            }
        )
//        startRefreshTokenManager()
        startSmsListener()
    }

    private fun openStartDestination(sourceIntent: Intent?) {
        navigator.open(
            destination = resolveStartDestination(sourceIntent),
            clearBackStack = true,
            addToBackStack = false,
            transition = AppNavigationTransition.Fade,
        )
    }

    private fun resolveStartDestination(sourceIntent: Intent?): AppDestination {
        val startDestination = sourceIntent?.getStringExtra(EXTRA_START_DESTINATION) ?: START_SPLASH
        return when (startDestination) {
            START_HOME -> AppDestination.HomeArg()
            START_LOGIN -> AppDestination.Login(
                showSessionExpired = sourceIntent?.getBooleanExtra(
                    EXTRA_SHOW_SESSION_EXPIRED,
                    false,
                ) == true,
            )

            else -> AppDestination.Splash
        }
    }

    private fun activeDestination(): AppDestination {
        return navigator.currentDestination ?: resolveStartDestination(intent)
    }

    private fun startSmsListener() {
        SmsRetriever.getClient(this).startSmsRetriever()
    }

    companion object {
        private const val EXTRA_START_DESTINATION = "extra_start_destination"
        private const val EXTRA_SHOW_SESSION_EXPIRED = "extra_show_session_expired"

        private const val START_SPLASH = "start_splash"
        private const val START_LOGIN = "start_login"
        private const val START_HOME = "start_home"

        @JvmStatic
        fun intent(
            context: Context,
            startDestination: String = START_SPLASH,
            showSessionExpired: Boolean = false,
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_START_DESTINATION, startDestination)
                putExtra(EXTRA_SHOW_SESSION_EXPIRED, showSessionExpired)
            }
        }

        @JvmStatic
        fun loginIntent(
            context: Context,
            showSessionExpired: Boolean = false,
        ): Intent {
            return intent(
                context = context,
                startDestination = START_LOGIN,
                showSessionExpired = showSessionExpired,
            )
        }

        @JvmStatic
        fun homeIntent(context: Context): Intent {
            return intent(
                context = context,
                startDestination = START_HOME,
            )
        }
    }
}

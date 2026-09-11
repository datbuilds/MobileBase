package com.mobile.base.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.graphics.Color
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.media3.common.BuildConfig
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.inject
import com.mobile.base.R
import com.mobile.base.MobileBaseApplication
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.dialog.DialogWarningAccessibilityPermission
import com.mobile.base.base.dialog.DialogWarningDeviceRoot
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.returnActivity
import com.mobile.base.utils.extensions.toast
import com.mobile.base.utils.refreshTK.RefreshTokenManager
import com.mobile.base.utils.view.dialog.ProgressDialogUtil
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.core.core.security.detectRoot.RootUtils
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage

abstract class BaseActivity<T : ViewBinding>(private val inflate: (LayoutInflater) -> T) :
    AppCompatActivity() {

    private lateinit var accessibilityManager: AccessibilityManager
    private val useCaseRefreshToken: UseCaseRefreshToken by inject()
    private val storage: AndroidSecureStorage by inject()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Kiểm tra xem có dialog error đang hiển thị không
            val errorDialog =
                supportFragmentManager.findFragmentByTag(BaseErrorDialog.TAG) as? BaseErrorDialog
            if (errorDialog != null) {
                if (!errorDialog.isCancelable) {
                    return
                }
            }

            // Xử lý back press theo từng activity
            when {
                shouldUseDoubleBackToExit() -> handleDoubleBackPress()
                shouldExitAppOnBackImmediately() -> finishAffinity()
                else -> onBackPressedAction()
            }
        }
    }

    private val handlerInactivity = Handler(Looper.getMainLooper())
    private val runnableInActivity = Runnable {
        println("${currentActivity()} Hết thời gian người dùng ko chạm vào màn hinh")
        inactivityTimeout()
    }

    // Double back press variables
    private var doubleBackToExitPressedOnce = false
    private val doubleBackHandler = Handler(Looper.getMainLooper())
    private val doubleBackRunnable = Runnable { doubleBackToExitPressedOnce = false }

    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        // Làm mờ / ẩn app trong Recent Apps
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
        _binding = inflate(layoutInflater)
        setContentView(binding.root)
        configureFullscreen()
        handleSavedState(savedInstanceState)
        initView()
        initListener()
        lifecycle.addObserver(ActivityLifeCycleObserver { initObserve() })

        //  checkThreadPolicy()
       checkSecurityApp()

        // Chỉ start RefreshTokenManager ở những activity cần thiết
//        if (shouldStartRefreshTokenManager()) {
            startRefreshTokenManager()
//        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        registerScreenReceiver()

        // Chặn toàn bộ overlay trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setHideOverlayWindows(true)
        }
    }

    private fun configureFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) configureFullscreen()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 1. Giữ nguyên logic bảo mật (Chống Tapjacking)
        if (ev?.flags?.and(MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0) {
            return false
        }

        // 2. Thêm logic ẩn bàn phím/focus khi click ra ngoài
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                // Kiểm tra tọa độ chạm có nằm ngoài EditText không
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }

        // 3. Luôn gọi super để các Fragment và View con nhận được sự kiện
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        println("${this.javaClass.simpleName} -> onResume() called")
        checkSecurityApp()
        startUserInteractionTimer()

        if (shouldStartRefreshTokenManager()) {
            RefreshTokenManager.updateActivity(this)
        }
    }

    override fun onPause() {
        super.onPause()
        println("${this.javaClass.simpleName} -> onPause() called")
        stopInactivityTimer()
    }

    override fun onStop() {
        dismissTransientUi()
        super.onStop()
    }

    /** Được gọi mỗi khi có sự tương tác của người dùng với màn hình */
    override fun onUserInteraction() {
        super.onUserInteraction()
        resetUserInteractionTimer()
    }

    private fun startUserInteractionTimer() {
        if (shouldHandleInactivityTimer()) {
            handlerInactivity.postDelayed(runnableInActivity, mTime)
        }
    }

    private fun resetUserInteractionTimer() {
        lastInteractionTime = System.currentTimeMillis()
        handlerInactivity.removeCallbacks(runnableInActivity)
        startUserInteractionTimer()
        println("${currentActivity()} Hủy bỏ bộ hẹn giờ hiện tại và bắt đầu một bộ hẹn giờ mới")
    }

    private fun stopInactivityTimer() {
        handlerInactivity.removeCallbacks(runnableInActivity)
    }

    private fun inactivityTimeout() {
        if (!shouldHandleInactivityTimer() || isSessionExpiryExempt()) {
            return
        }
        if (currentActivity() !in activityNotExpires()) {
            showDialogSessionExpire()
        } else {
            sessionExpired()
        }
    }

    open fun showDialogSessionExpire() {
        logout(isShowSessionExpired = true)
    }

    override fun onDestroy() {
        stopInactivityTimer()
        onBackPressedCallback.remove()
        doubleBackHandler.removeCallbacks(doubleBackRunnable)
//        RefreshTokenManager.updateActivity(null) // Clear activity reference
        unregisterReceiver(screenReceiver)
        super.onDestroy()
    }

    private fun activityNotExpires(): List<String> = emptyList()

    private fun currentActivity() =
        (application as MobileBaseApplication).currentActivity?.javaClass?.simpleName

    abstract fun initView()
    abstract fun initListener()
    abstract fun initObserve()

    open fun handleSavedState(savedInstanceState: Bundle?) {}
    open fun sessionExpired() {}
    open fun dismissTransientUi() {
        ProgressDialogUtil.dismiss()
    }

    protected fun startRefreshTokenManager() {
        RefreshTokenManager.start(
            activity = this,
            useCase = useCaseRefreshToken,
            storage = storage
        )
    }

    fun logout(isShowSessionExpired: Boolean = false) {
        storage.resetToken()
        finishAffinity()
        returnActivity(buildLoginIntent(isShowSessionExpired))
    }

    private fun checkThreadPolicy() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }
    }

    private fun checkSecurityApp() {
        if (RootUtils.isDeviceRooted(this)) {
            showDialogWarningDeviceRoot()
        } else {
            checkAccessibilityPermission()
        }
    }

    open fun checkAccessibilityPermission() {
        if (isAccessibilityPermissionEnable()) {
            showDialogWarningAccessibilityPermission()
        }
    }

    open fun isAccessibilityPermissionEnable(): Boolean {
        accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        return accessibilityManager.isEnabled
    }

    open fun showDialogWarningAccessibilityPermission() {
        val dialog = DialogWarningAccessibilityPermission.Build { finish() }.build()
        dialog.isCancelable = false
        if (!isDialogShowing(DialogWarningAccessibilityPermission.TAG)) {
            dialog.show(supportFragmentManager, DialogWarningAccessibilityPermission.TAG)
        }
    }

    open fun showDialogWarningDeviceRoot() {
        val dialog = DialogWarningDeviceRoot.Build { finish() }.build()
        dialog.isCancelable = false
        if (!isDialogShowing(DialogWarningDeviceRoot.TAG)) {
            dialog.show(supportFragmentManager, DialogWarningDeviceRoot.TAG)
        }
    }

    open fun isDialogShowing(tag: String): Boolean {
        return supportFragmentManager.findFragmentByTag(tag) != null
    }

    override fun attachBaseContext(newBase: Context?) {
        val context =
            newBase?.let { context ->
                val config = context.resources.configuration
                if (config.fontScale > 1F) {
                    config.fontScale = 1f
                    context.createConfigurationContext(config)
                } else context
            }

        super.attachBaseContext(context)
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    // ---------------------------Receiver------------------------------//

    private fun registerScreenReceiver() {
        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
        registerReceiver(screenReceiver, filter)
    }

    private var mTime = Const.TIME_NO_ACTION * 60 * 1000L
    private var lastInteractionTime: Long = 0
    private val screenReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        lastInteractionTime = System.currentTimeMillis()
                        stopInactivityTimer()
                    }

                    Intent.ACTION_SCREEN_ON -> {
                        if (!shouldHandleInactivityTimer()) {
                            return
                        }
                        val currentTime = System.currentTimeMillis()
                        val elapsedTime = currentTime - lastInteractionTime
                        val remainingTime = mTime - elapsedTime
                        println("Thời gian còn lại ACTION_SCREEN_ON : $remainingTime")
                        if (remainingTime <= 0) {
                            inactivityTimeout()
                        } else {
                            handlerInactivity.postDelayed(runnableInActivity, remainingTime)
                        }
                    }
                }
            }
        }

    // Override để control việc start RefreshTokenManager
    protected open fun shouldStartRefreshTokenManager(): Boolean {
        return false // Default: không start
    }

    protected open fun shouldUseDoubleBackToExit(): Boolean {
        return false
    }

    protected open fun shouldExitAppOnBackImmediately(): Boolean {
        return false
    }

    protected open fun shouldHandleInactivityTimer(): Boolean {
        return false
    }

    protected open fun isSessionExpiryExempt(): Boolean {
        return false
    }

    protected open fun buildLoginIntent(isShowSessionExpired: Boolean): Intent {
        return MainActivity.loginIntent(this, isShowSessionExpired)
    }

    // Method để xử lý back press mặc định
    open fun onBackPressedAction() {
        finish()
    }

    private fun handleDoubleBackPress() {
        if (doubleBackToExitPressedOnce) {
            // Lần thứ 2: thoát app
            doubleBackHandler.removeCallbacks(doubleBackRunnable)
            doubleBackToExitPressedOnce = false
            finishAffinity()
        } else {
            // Lần thứ 1: hiển thị toast
            doubleBackToExitPressedOnce = true
            toast("${getString(R.string.app_name)}: Vuốt lần nữa để thoát")
            // Reset sau 2 giây nếu không có lần back thứ 2
            doubleBackHandler.postDelayed(doubleBackRunnable, 2000)
        }
    }
}

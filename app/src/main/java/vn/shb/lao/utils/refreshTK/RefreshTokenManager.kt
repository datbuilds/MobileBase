package vn.shb.lao.utils.refreshTK

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseErrorDialog
import vn.shb.lao.utils.extensions.returnActivity
import java.util.concurrent.TimeUnit

object RefreshTokenManager {
    private var handler: Handler? = null
    private var refreshRunnable: Runnable? = null
    private var applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentActivity: FragmentActivity? = null
    private var isErrorShowing: Boolean = false

    @SuppressLint("StaticFieldLeak")
    private var currentStorage: AndroidSecureStorage? = null
    private var currentUseCase: UseCaseRefreshToken? = null

    fun start(
        activity: FragmentActivity,
        useCase: UseCaseRefreshToken,
        storage: AndroidSecureStorage,
    ) {
        stop()

        currentStorage = storage
        currentUseCase = useCase
        updateActivity(activity)

        val refreshIntervalMillis = (storage.getExpireTime()).minus(1) * 60 * 1000L
        println("RFManager -> Token will be refreshed every ${refreshIntervalMillis / 1000} seconds")

        handler = Handler(Looper.getMainLooper())
        refreshRunnable = object : Runnable {
            override fun run() {
                applicationScope.launch {
                    val s = currentStorage
                    val u = currentUseCase
                    if (s != null && u != null) {
                        performTokenRefresh(s, u)
                    }
                }
                handler?.postDelayed(this, refreshIntervalMillis)
            }
        }
        handler?.postDelayed(refreshRunnable!!, refreshIntervalMillis)
    }

    private suspend fun performTokenRefresh(
        storage: AndroidSecureStorage,
        useCase: UseCaseRefreshToken
    ) {
        try {
            println("RFManager -> Starting token refresh...")
            // Get secure refresh token
            val rfToken = storage.getRfToken()
            if (rfToken.isEmpty()) {
                println("RFManager -> No secure refresh token available")
                storage.setTokenInvalid(true)
                return
            }

            val params = UseCaseRefreshToken.Params(refreshToken = rfToken)
            useCase(params).collectLatest { refreshResult ->
                refreshResult.onSuccess {
                    synchronized(storage) {
                        storage.apply {
                            setToken(it.access_token)
                            storage.setRfToken(it.refresh_token)
                            setExpireTime(TimeUnit.SECONDS.toMinutes(it.expireIn()).toInt())
                            setTokenInvalid(false)
                        }
                    }
                    isErrorShowing = false // Reset flag khi refresh thành công

                    scheduleNextRefresh()

                    println("RFManager -> Token refreshed successfully")
                }
                refreshResult.onFailure {
                    storage.setTokenInvalid(true)
                    throw Exception("RFManager -> Token refresh failed: ${it.errMessage}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("RFManager -> Exception during token refresh: ${e.message}")
        }
    }

    private fun scheduleNextRefresh() {
        val storage = currentStorage ?: return
        val refreshIntervalMillis = (storage.getExpireTime()).minus(1) * 60 * 1000L
        println("RFManager -> Scheduling next refresh in ${refreshIntervalMillis / 1000} seconds")

        handler?.removeCallbacks(refreshRunnable!!)
        handler?.postDelayed(refreshRunnable!!, refreshIntervalMillis)
    }

    private fun showErrorDialog(activity: FragmentActivity, storage: AndroidSecureStorage) {
        // Kiểm tra nếu dialog đã đang hiển thị thì không hiển thị lại
        if (isErrorShowing) {
            println("RFManager -> Error dialog already showing, skipping...")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            println("RFManager -> Activity is finishing/destroyed, cannot show dialog")
            return
        }

        val fm = activity.supportFragmentManager
        if (fm.isStateSaved) {
            println("RFManager -> State already saved, cannot show dialog safely")
            return
        }

        // Kiểm tra xem dialog đã tồn tại chưa
        val existingDialog = fm.findFragmentByTag(BaseErrorDialog.TAG)
        if (existingDialog != null) {
            println("RFManager -> Error dialog already exists, skipping...")
            return
        }

        isErrorShowing = true // Set flag trước khi hiển thị dialog

        val dialogError = BaseErrorDialog.Build(
            activity.getString(vn.shb.lao.localization.R.string.title_noti),
            activity.getString(vn.shb.lao.localization.R.string.content_warning),
            activity.getString(vn.shb.lao.localization.R.string.shb_retry_login),
            R.drawable.ic_bs_notification,
            onClose = {
                // Chỉ thực hiện logout khi user click action
                isErrorShowing = false
                stop()
                synchronized(storage) {
                    storage.resetToken()
                }
                clearPref(activity)
            },
            onDismiss = {
                // Reset flag khi dialog bị dismiss mà không phải do user click
                isErrorShowing = false
                println("RFManager -> Error dialog dismissed")
            },
            allowDismiss = false // Không cho phép dismiss bằng back press hoặc touch
            // outside
        ).build()

        dialogError.isCancelable = false

        try {
            dialogError.show(fm, BaseErrorDialog.TAG)
            println("RFManager -> Error dialog shown successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            println("RFManager -> Error showing dialog: ${e.message}")
            isErrorShowing = false // Reset flag nếu có lỗi
            synchronized(storage) {
                storage.resetToken()
            }
        }
    }

    fun updateActivity(activity: FragmentActivity?) {
        currentActivity = activity
        println("RFManager -> activity updated: ${activity?.javaClass?.simpleName}")

        val storage = currentStorage
        if (storage != null && storage.isTokenInvalid() && activity != null && !isErrorShowing) {
            println("RFManager -> Token is invalid, showing error dialog")
            showErrorDialog(activity, storage)
        } else {
            println(
                "RFManager -> Skipping error dialog: " + "storage=${storage != null}, " + "tokenInvalid=${storage?.isTokenInvalid()}, " + "activity=${activity != null}, " + "dialogShowing=$isErrorShowing"
            )
        }
    }

    private fun clearPref(activity: FragmentActivity) {
        try {
            if (!activity.isFinishing && !activity.isDestroyed) {
                activity.apply {
                    finishAffinity()
                    returnActivity(LoginActivity.intent(this))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        refreshRunnable?.let { runnable ->
            handler?.removeCallbacks(runnable)
        }
        handler = null
        refreshRunnable = null
        currentActivity = null
        currentStorage = null
        currentUseCase = null
        isErrorShowing = false // Reset flag khi stop
        println("RFManager -> stopped")
    }
}

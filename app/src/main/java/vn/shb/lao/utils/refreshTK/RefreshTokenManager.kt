package vn.shb.lao.utils.refreshTK

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.BuildConfig
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper
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
    private var currentUseCaseWso2: UseCaseRefreshTokenWso2? = null

    fun start(
        activity: FragmentActivity,
        useCase: UseCaseRefreshToken,
        useCaseWso2: UseCaseRefreshTokenWso2,
        storage: AndroidSecureStorage
    ) {
        stop()

        currentStorage = storage
        currentUseCase = useCase
        currentUseCaseWso2 = useCaseWso2
        updateActivity(activity)

        val refreshIntervalMillis = (storage.getExpireTime()).minus(1) * 60 * 1000L
        println("RFManager333 -> Expire ${storage.getExpireTime()}")
        println("RFManager333 -> Token will be refreshed every ${refreshIntervalMillis / 1000} seconds")

        handler = Handler(Looper.getMainLooper())
        refreshRunnable = object : Runnable {
            override fun run() {
                applicationScope.launch {
                    val s = currentStorage
                    val u = currentUseCaseWso2
                    if (BuildConfig.FLAVOR == "pro") {
                        if (s != null && currentUseCase != null) {
                            performTokenRefresh(s, currentUseCase!!) {
                                activity.runOnUiThread {
                                    updateActivity(activity)
                                }
                            }
                        }
                    } else {
                        if (s != null && u != null) {
                            performTokenRefreshWso2(s, activity, u) {
                                activity.runOnUiThread {
                                    updateActivity(activity)
                                }
                            }
                        }
                    }
                }
                handler?.postDelayed(this, refreshIntervalMillis)
            }
        }
        handler?.postDelayed(refreshRunnable!!, refreshIntervalMillis)
    }

    private suspend fun performTokenRefreshWso2(
        storage: AndroidSecureStorage,
        activity: FragmentActivity,
        useCaseWso2: UseCaseRefreshTokenWso2,
        onFail: (() -> Unit)? = null
    ) {
        try {
            println("RFManager -> Starting token refresh...")
            // Get secure refresh token
            val rfToken = storage.getRfTokenWso2()
            if (rfToken.isEmpty()) {
                println("RFManager -> No secure refresh token available")
                storage.setTokenInvalidWso2(true)
                return
            }

            val paramsWso2 = UseCaseRefreshTokenWso2.InputParams(
                BuildConfig.AUTHORIZATION, UseCaseRefreshTokenWso2.Params(
                    grant_type = "refresh_token",
                    refresh_token = rfToken
                )
            )
            useCaseWso2(paramsWso2).collectLatest { refreshResult ->
                refreshResult.onSuccess { response ->
                    synchronized(storage) {
                        storage.apply {
                            setTokenWso2(response.access_token)
                            updateExpireTime(
                                TimeUnit.SECONDS.toMinutes(response.expireIn()).toInt()
                            )
                            setTokenInvalidWso2(false)
                            setRfTokenWso2(response.refresh_token)
                            applicationScope.launch {
                                val s = currentStorage
                                val u = currentUseCase
                                if (s != null && u != null) {
                                    performTokenRefresh(s, u) {
                                        activity.runOnUiThread {
                                            updateActivity(activity)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    isErrorShowing = false // Reset flag khi refresh thành công

                    scheduleNextRefresh()

                    println("RFManager -> Token refreshed successfully")
                }
                refreshResult.onFailure {
                    storage.setTokenInvalidWso2(true)
                    onFail?.invoke()
                    throw Exception("RFManager -> Token refresh failed: ${it.errMessage}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("RFManager -> Exception during token refresh: ${e.message}")
        }
    }

    private suspend fun performTokenRefresh(
        storage: AndroidSecureStorage,
        useCase: UseCaseRefreshToken,
        onFail: (() -> Unit)? = null
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
                refreshResult.onSuccess { response ->
                    synchronized(storage) {
                        storage.apply {
                            setToken(response.access_token)
                            updateExpireTime(
                                TimeUnit.SECONDS.toMinutes(response.expireIn()).toInt()
                            )
                            setTokenInvalid(false)
                            setRfToken(response.refresh_token)
                        }
                    }
                    isErrorShowing = false // Reset flag khi refresh thành công

                    scheduleNextRefresh()

                    println("RFManager -> Token refreshed successfully")
                }
                refreshResult.onFailure {
                    storage.setTokenInvalid(true)
                    onFail?.invoke()
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

        BottomSheetDialogHelper(activity).message(
            title = activity.getString(R.string.notification),
            message = activity.getString(R.string.processingError),
            textPositive = activity.getString(R.string.close),
            positiveAction = {
                isErrorShowing = false
                stop()
                synchronized(storage) {
                    storage.resetToken()
                }
                clearPref(activity)
            }
        )
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

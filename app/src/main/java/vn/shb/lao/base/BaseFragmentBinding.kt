package vn.shb.lao.base

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.platform.MaterialFadeThrough
import org.koin.android.ext.android.inject
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserConverters
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.utils.extensions.CustomToastShowOnTop
import vn.shb.lao.utils.extensions.navigation.safeNavigate
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.refreshTK.RefreshTokenManager

abstract class BaseFragmentBinding<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    protected val storage: AndroidSecureStorage by inject()

    companion object {
        private const val DELAY_MILLIS = 500L
        private var previousClickTimeMillis = 0L
    }

    private var _binding: T? = null

    val binding: T
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateMethod(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSavedState(savedInstanceState)
        initView(view)
        initListener()
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver { initObserve() })
    }

    override fun onResume() {
        super.onResume()
        playFadeThrough() // Kích hoạt lại animation mỗi khi Fragment trở lại trạng thái RESUMED
    }

    private fun setBaseTransitions() {
        // Gán transition gốc để Fragment luôn có hiệu ứng enter/re‑enter
        val fade = MaterialFadeThrough().apply { duration = 250 }
        enterTransition = fade
        reenterTransition = fade
    }

    /**
     * Gọi hàm này để kích hoạt lại hiệu ứng MaterialFadeThrough mỗi khi Fragment được hiển thị
     * (ViewPager page visible, popBackStack, …). Override nếu muốn transition khác.
     */
    protected open fun playFadeThrough() {
        // Parent của root view là ViewGroup – nơi diễn ra Transition
        (binding.root.parent as? ViewGroup)?.let { parent ->
            val transition = MaterialFadeThrough().apply { duration = 300 }
            transition.addTarget(binding.root)
            TransitionManager.beginDelayedTransition(parent, transition)
        }
    }

    open fun handleSavedState(savedInstanceState: Bundle?) {}

    abstract fun initView(view: View)

    abstract fun initListener()

    abstract fun initObserve()

    open fun addFlag() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    open fun clearFlag() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    open fun isDialogShowing(TAG: String): Boolean {
        return requireActivity().supportFragmentManager.findFragmentByTag(TAG) != null
    }

    @SuppressLint("InlinedApi")
    open fun hideSystemUi(view: View) {
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowInsetsControllerCompat(window, view)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    protected fun safeAction(action: () -> Unit) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
            previousClickTimeMillis = currentTimeMillis
            action()
        }
    }

    fun safeNavigate(
        @IdRes currentDestinationId: Int,
        @IdRes actionId: Int,
        bundle: Bundle? = null,
        options: NavOptions? = null
    ) {
        findNavController().safeNavigate(currentDestinationId, actionId, bundle, options)
    }

    fun safeNavigate(deepLink: Uri) {
        try {
            findNavController().navigate(deepLink)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCurrentUser() = UserConverters.stringToUserInfo(storage.getUserInfo())

    open fun setNavigationBarColor(color: Int) {
        requireActivity().window.navigationBarColor = color
    }

    open fun enableFullScreen() {
        val window = requireActivity().window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    open fun disableFullScreen() {
        val window = requireActivity().window
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

    open fun showDialogError(
        errCode: String = "",
        title: String = getString(vn.shb.lao.localization.R.string.title_noti),
        message: String = "",
        tvAction: String = getString(vn.shb.lao.localization.R.string.ui_common_close),
        icon: Int = R.drawable.ic_bs_notification,
        isCancelable: Boolean = true,
        onAction: (() -> Unit)? = null
    ) {
        val des =
            if (message == "closed") {
                "Có lỗi trong quá trình kết nối hệ thống. Vui lòng thực hiện lại sau"
            } else {
                message
            }

        val dialogError =
            BaseErrorDialog.Build(
                title = title,
                message = des,
                tvAction = tvAction,
                icon = icon,
                onClose = {
                    if (errCode == "999") {
                        logout()
                    } else {
                        if (onAction != null) {
                            onAction()
                        }
                    }
                },
                allowDismiss = isCancelable
            )
                .build()

        dialogError.isCancelable = false

        if (isDialogShowing(BaseErrorDialog.TAG)) {
            return
        } else {
            dialogError.show(childFragmentManager, BaseErrorDialog.TAG)
        }
    }

    private fun logout() {
        storage.resetUser()
        RefreshTokenManager.stop()
        finishAffinity(requireActivity())
        returnActivity(LoginActivity.intent(requireActivity()))
    }

    open fun showNotificationOnTop(
        view: View,
        icon: Int = R.drawable.ic_close,
        message: String,
        background: Int = R.drawable.bg_custom_success,
        duration: Long = 5000L
    ) {
        val toast =
            CustomToastShowOnTop(
                requireContext(),
                view,
                icon = icon,
                message = message,
                background = background,
                duration = duration
            )
        toast.show()
    }

    open fun showDialogAlert(
        view: View,
        message: String,
        duration: Long = 5000L
    ) {
//        context?.let { AlertDialogUtil.message(it, getString(R.string.notificationLabel), message = getString(R.string.)) }
    }

    // Removing the binding reference when not needed is recommended as it avoids memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

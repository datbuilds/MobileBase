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
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
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
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.retrofit.SafeExecute
import vn.shb.core.core.retrofit.SafeExecute.Companion.AUTH_006
import vn.shb.core.core.retrofit.SafeExecute.Companion.HTTP_NOT_FOUND
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserConverters
import vn.shb.data.entities.login.UserLog
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.utils.extensions.navigation.safeNavigate
import vn.shb.lao.utils.extensions.returnActivity
import vn.shb.lao.utils.refreshTK.RefreshTokenManager
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

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

    private val listErrorLogout = listOf(AUTH_006)

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

    fun backPress() {
        findNavController().popBackStack()
    }

    fun safeNavigate(deepLink: Uri) {
        try {
            findNavController().navigate(deepLink)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCurrentUser() = UserConverters.stringToUserInfo(storage.getUserLog())
    fun setCurrentUser(user: UserLog) = storage.setUserLog(UserConverters.userInfoToString(user))

    fun getPathAvatarUser(key: String? = getCurrentUser()?.customerId) =
        key?.let { storage.getPathAvatarUser(it) }

    fun setPathAvatarUser(path: String, key: String? = getCurrentUser()?.customerId) =
        key?.let { storage.setPathAvatarUser(it, path) }

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
        reason: Reason,
        onAction: (() -> Unit)? = null
    ) {
        var message = reason.errMessage
        if (reason.errorCode == SafeExecute.HTTP_NOT_FOUND) {
            message = getString(R.string.processingError)
        }
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.notification),
            message = message,
            textPositive = getString(R.string.close),
            positiveAction = {
                onAction?.invoke()
            }
        )
    }

    fun handleErrorHome(error: Reason?) {
        if (error != null) {
            if (listErrorLogout.contains(error.errorCode)) {
                logout()
            }
            showDialogError(error)
        }
    }


    fun getColor(@ColorRes colorId: Int) = ContextCompat.getColor(requireContext(), colorId)

    fun logout() {
        storage.resetUser()
        RefreshTokenManager.stop()
        finishAffinity(requireActivity())
        returnActivity(LoginActivity.intent(requireActivity()))
    }

    // Removing the binding reference when not needed is recommended as it avoids memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

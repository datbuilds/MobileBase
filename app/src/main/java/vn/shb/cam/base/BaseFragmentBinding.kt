package vn.shb.cam.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.platform.MaterialFadeThrough
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.cam.R
import vn.shb.cam.activity.MainActivity
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.navigation.NavigationTransition
import vn.shb.cam.navigation.requireNavigator
import vn.shb.cam.screens.home.HomeViewModel
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.returnActivity
import vn.shb.cam.utils.refreshTK.RefreshTokenManager
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.retrofit.SafeExecute.Companion.AUTH_001
import vn.shb.core.core.retrofit.SafeExecute.Companion.AUTH_002
import vn.shb.core.core.retrofit.SafeExecute.Companion.AUTH_006
import vn.shb.core.core.retrofit.SafeExecute.Companion.HTTP_NOT_FOUND
import vn.shb.core.core.retrofit.SafeExecute.Companion.HTTP_UNAUTHORIZED
import vn.shb.core.core.retrofit.SafeExecute.Companion.TOKEN_EXPIRE
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserConverters

abstract class BaseFragmentBinding<T : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    protected val storage: AndroidSecureStorage by inject()

    protected val homeViewModel: HomeViewModel by sharedViewModel()

    private var _binding: T? = null
    public val binding: T
        get() = requireNotNull(_binding)

    private val listErrorLogout = listOf(AUTH_006, AUTH_001, AUTH_002)
    private val listErrorShowErrorAndLogout =
        listOf(HTTP_UNAUTHORIZED.toString(), HTTP_NOT_FOUND, TOKEN_EXPIRE)

    open fun isPaddingBottom() = false
    protected open fun useBaseFadeThrough() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useBaseFadeThrough()) {
            setBaseTransitions()
        } else {
            enterTransition = null
            reenterTransition = null
        }
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
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver {
            initObserve()
            observerStateError()
        })
    }

    private fun insertPaddingView() {
        if (!isPaddingBottom()) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                v.updatePadding(
                    top = statusBarHeight.top,
                    bottom = statusBarHeight.bottom + 60
                )
                insets
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updatePadding(
                    top = statusBarInsets.top,
                    bottom = navBarInsets.bottom + 20
                )
                insets
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (useBaseFadeThrough()) {
            playFadeThrough() // Kích hoạt lại animation mỗi khi Fragment trở lại trạng thái RESUMED
        }
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

    open fun clearFlag() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    fun safeNavigate(
        destination: AppDestination,
        clearBackStack: Boolean = false,
        addToBackStack: Boolean = true,
        transition: NavigationTransition? = null,
    ) {
        requireNavigator().open(
            destination = destination,
            clearBackStack = clearBackStack,
            addToBackStack = addToBackStack,
            transition = transition,
        )
    }

    fun backPress() {
        val handled = requireNavigator().goBack()
        if (!handled) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun popBackTo(
        destination: AppDestination,
        inclusive: Boolean = false,
    ) {
        val handled = requireNavigator().popTo(destination, inclusive)
        if (!handled) {
            backPress()
        }
    }

    fun safeNavigate(deepLink: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, deepLink))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCurrentUser() = UserConverters.stringToUserInfo(storage.getUserLog())

    fun getPathAvatarUser(key: String? = getCurrentUser()?.customerId) =
        key?.let { storage.getPathAvatarUser(it) }

    fun setPathAvatarUser(path: String, key: String? = getCurrentUser()?.customerId) =
        key?.let { storage.setPathAvatarUser(it, path) }


    open fun showDialogError(
        reason: Reason,
        onAction: (() -> Unit)? = null
    ) {
        if (reason.errorCode == ApiConst.FUN_017 || reason.errorCode == getString(R.string.errorCode)) {
            BottomSheetDialogHelper(requireContext()).messageErrorCode(reason) {
                onAction?.invoke()
            }
            return
        }
        var message = reason.errMessage
        if (reason.errorCode == HTTP_NOT_FOUND && reason.errorCode == TOKEN_EXPIRE) {
            message = getString(R.string.processingError)
        }
        showErrorMessageOnly(message, onAction)
    }

    open fun showErrorMessageOnly(message: String, onAction: (() -> Unit)? = null) {
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.notification),
            message = message,
            textPositive = getString(R.string.close),
            positiveAction = {
                onAction?.invoke()
            }
        )
    }

    protected fun handleErrorHome(error: Reason?, onAction: (() -> Unit)? = null) {
        if (error != null) {
            if (listErrorLogout.contains(error.errorCode)) {
                logout()
            } else if (listErrorShowErrorAndLogout.contains(error.errorCode)) {
                showDialogError(error, {
                    onAction?.invoke()
                    logout()
                })
            } else {
                showDialogError(error, {
                    onAction?.invoke()
                })
            }
        }
    }

    fun logout() {
        storage.resetToken()
        finishAffinity(requireActivity())
        returnActivity(MainActivity.loginIntent(requireActivity()))
    }

    private fun observerStateError() {
        launchRepeatOnLifecycle {
            launch {
                homeViewModel.stateError.collect { error ->
                    handleErrorHome(error)
                }
            }
        }
    }


    fun getColor(@ColorRes colorId: Int) = ContextCompat.getColor(requireContext(), colorId)

    // Removing the binding reference when not needed is recommended as it avoids memory leak
    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}

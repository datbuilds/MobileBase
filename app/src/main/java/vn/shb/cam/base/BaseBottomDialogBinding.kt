package vn.shb.cam.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.CustomToastShowOnTop
import vn.shb.cam.utils.extensions.navigation.safeNavigate

abstract class BaseBottomDialogBinding<T : ViewBinding>(
        private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BottomSheetDialogFragment() {

    companion object {
        private const val DELAY_MILLIS = 500L
        private var previousClickTimeMillis = 0L
    }

    private var _binding: T? = null

    val binding: T
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog_Rounded)
    }

    override fun onStart() {
        super.onStart()
        val mBottomBehavior = BottomSheetBehavior.from<View>(view?.parent as View)
        mBottomBehavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        mBottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val screenHeight = resources.displayMetrics.heightPixels
        val desiredHeight = screenHeight * 2 / 3
        mBottomBehavior.peekHeight = desiredHeight
        mBottomBehavior.expandedOffset = screenHeight - desiredHeight

        // Kiểm tra nếu dialog không cho phép dismiss thì set touch outside không dismiss
        if (!isCancelable) {
            mBottomBehavior.isHideable = false
            mBottomBehavior.isDraggable = false
        }
    }

    open fun showBottomSheetSmooth(bottomSheet: View) {
        bottomSheet.visibility = View.VISIBLE

        // Đặt vị trí ban đầu: ở ngoài màn hình (dịch xuống)
        bottomSheet.translationY = bottomSheet.height.toFloat()

        // Tạo animation kiểu lò xo
        val springAnim = SpringAnimation(bottomSheet, DynamicAnimation.TRANSLATION_Y, 0f)
        springAnim.spring.stiffness = SpringForce.STIFFNESS_MEDIUM
        springAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        springAnim.start()
    }

    open fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context?.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager)
                ?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSavedState(savedInstanceState)
        initView(view)
        initListener()
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver { initObserve() })

        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val focusedView = dialog?.currentFocus
                if (focusedView is EditText) {
                    val outRect = Rect()
                    focusedView.getGlobalVisibleRect(outRect)

                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        focusedView.clearFocus()
                        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    }
                }
            }
            // Trả về false để không chặn các sự kiện click/scroll của các view con
            false
        }
    }

    open fun handleSavedState(savedInstanceState: Bundle?) {}

    abstract fun initView(view: View)

    abstract fun initListener()

    abstract fun initObserve()

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

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
            if (manager.findFragmentByTag(tag) == null) {
                fragmentTransaction.add(this, tag)
                fragmentTransaction.commitAllowingStateLoss()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
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

    open fun showDialogError(
            errCode: String = "",
            title: String = getString(vn.shb.cam.localization.R.string.title_noti),
            message: String = "",
            tvAction: String = getString(vn.shb.cam.localization.R.string.ui_common_close),
            icon: Int = R.drawable.ic_bs_notification,
            isCancelable: Boolean = true,
            onClose: (() -> Unit?)? = null
    ) {
        val dialogError =
                BaseErrorDialog.Build(
                                title = title,
                                message = message,
                                tvAction = tvAction,
                                icon = icon,
                                onClose = onClose,
                                allowDismiss = isCancelable
                        )
                        .build()

        dialogError.isCancelable = false

        val prev = requireActivity().supportFragmentManager.findFragmentByTag(BaseErrorDialog.TAG)
        if (prev == null) {
            dialogError.show(childFragmentManager, BaseErrorDialog.TAG)
        } else {
            return
        }
    }

    // Removing the binding reference when not needed is recommended as it avoids memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

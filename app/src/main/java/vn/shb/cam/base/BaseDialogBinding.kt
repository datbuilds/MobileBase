package vn.shb.cam.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.navigation.safeNavigate

abstract class BaseDialogBinding<T : ViewBinding>(
        private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T
) : DialogFragment() {

    companion object {
        private const val DELAY_MILLIS = 500L
        private var previousClickTimeMillis = 0L
    }

    private var _binding: T? = null

    // This can be accessed by the child fragments
    // Only valid between onCreateView and onDestroyView
    val binding: T
        get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        // replaced _binding!! with binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSavedState(savedInstanceState)
        initView(view)
        initListener()
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver { initObserve() })
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

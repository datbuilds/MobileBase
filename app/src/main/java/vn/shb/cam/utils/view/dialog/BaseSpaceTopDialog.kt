package vn.shb.cam.utils.view.dialog

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.getActionBarHeight
import vn.shb.cam.utils.extensions.getNavigationBarHeight
import vn.shb.cam.utils.extensions.getScreenHeight
import vn.shb.cam.utils.extensions.getStatusBarHeight

abstract class BaseSpaceTopDialog<T : ViewBinding>(private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T) :
    DialogFragment() {

    private lateinit var mRootView: View

    private var behavior: BottomSheetBehavior<FrameLayout>? = null
    private val _stateKeyboard = MutableStateFlow(false)
    private val stateKeyboard: SharedFlow<Boolean> get() = _stateKeyboard

    private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rootHeight = mRootView.rootView.height
        val r = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)
        val navigationBarHeight = requireContext().getNavigationBarHeight()
        val isKeyboardShowing = r.bottom < (rootHeight - navigationBarHeight)
        _stateKeyboard.value = isKeyboardShowing
    }

    private var _binding: T? = null

    val binding: T get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        mRootView = binding.root
        return mRootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog =
            BottomSheetDialog(
                requireContext(),
                R.style.BottomDialog_Rounded_Keyboard
            )
        bottomSheetDialog.setOnShowListener {
            1
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
            bottomSheet?.let {
                val peekHeight = getPeekHeight()

                behavior = BottomSheetBehavior.from(bottomSheet)
                behavior?.skipCollapsed = true
                behavior?.peekHeight = peekHeight

                val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.height = peekHeight
                bottomSheet.layoutParams = layoutParams
            }
        }
        return bottomSheetDialog
    }

    protected fun setDraggable(isDraggable: Boolean) {
        behavior?.isDraggable = isDraggable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted { keyboardListener() }
    }

    private suspend fun keyboardListener() {
        stateKeyboard.collectLatest { keyboardShowing ->
            val screenHeight = requireContext().getScreenHeight()

            val rootHeight = mRootView.rootView.height

            val statusBarHeight = requireContext().getStatusBarHeight()
            val actionBarHeight = requireContext().getActionBarHeight()
            val navigationBarHeight = requireContext().getNavigationBarHeight()

            val r = Rect()
            mRootView.getWindowVisibleDisplayFrame(r)
            val heightDiff = rootHeight - navigationBarHeight - r.bottom

            val heightShow = if (keyboardShowing)
                rootHeight - statusBarHeight - navigationBarHeight - heightDiff
            else
                screenHeight - statusBarHeight - actionBarHeight - navigationBarHeight

            val layoutParams = view?.layoutParams as? FrameLayout.LayoutParams
            layoutParams?.height = heightShow
            view?.layoutParams = layoutParams
        }
    }

    override fun onDestroyView() {
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(layoutListener)
        super.onDestroyView()
        _binding = null
    }

    private fun getPeekHeight(): Int {
        val screenHeight = requireContext().getScreenHeight()

        val statusBarHeight = requireContext().getStatusBarHeight()
        val actionBarHeight = requireContext().getActionBarHeight()
        val navigationBarHeight = requireContext().getNavigationBarHeight()

        return screenHeight - actionBarHeight - statusBarHeight - navigationBarHeight
    }
}
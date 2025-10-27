//package vn.shb.lao.base
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.View
//import android.view.WindowManager
//import androidx.annotation.IdRes
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.fragment.app.Fragment
//import androidx.navigation.NavOptions
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.ui.NavigationUI
//import com.google.android.material.appbar.MaterialToolbar
//import vn.shb.lao.utils.extensions.navigation.safeNavigate
//
//abstract class BaseFragment : Fragment() {
//    companion object {
//        private const val DELAY_MILLIS = 500L
//        private var previousClickTimeMillis = 0L
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        handleSavedState(savedInstanceState)
//        initView(view)
//        initListener()
//        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver {
//            initObserve()
//        })
//    }
//
//    open fun handleSavedState(savedInstanceState: Bundle?) {}
//
//    abstract fun initView(view: View)
//
//    abstract fun initListener()
//
//    abstract fun initObserve()
//
//    @SuppressLint("InlinedApi")
//    open fun hideSystemUi(view: View) {
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
//        WindowInsetsControllerCompat(
//            requireActivity().window,
//            view
//        ).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
//    open fun showSystemUI() {
//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }
//
//    protected fun safeAction(action: () -> Unit) {
//        val currentTimeMillis = System.currentTimeMillis()
//
//        if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
//            previousClickTimeMillis = currentTimeMillis
//            action()
//        }
//    }
//
//    open fun safeNavigate(
//        @IdRes currentDestinationId: Int,
//        @IdRes actionId: Int,
//        bundle: Bundle? = null,
//        options: NavOptions? = null
//    ) {
//        findNavController().safeNavigate(currentDestinationId, actionId, bundle, options)
//    }
//
//    open fun setupActionBarWithNavController(toolbar: MaterialToolbar?) {
//        val activity = requireActivity() as AppCompatActivity
//        val navController = findNavController()
//
//        activity.setSupportActionBar(toolbar)
//        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
//        NavigationUI.setupActionBarWithNavController(activity, navController)
//    }
//}
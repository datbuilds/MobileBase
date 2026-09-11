package com.mobile.base.screens.splash.ui

import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentSplashBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.navigation.AppNavigationTransition
import com.mobile.base.navigation.requireNavigator

class SplashFragment :
    BaseFragmentBinding<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var hasScheduledNavigation = false

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) = Unit

    override fun initListener() = Unit

    override fun initObserve() = Unit

    override fun onResume() {
        super.onResume()
        if (hasScheduledNavigation) {
            return
        }
        hasScheduledNavigation = true
        viewLifecycleOwner.lifecycleScope.launch {
            delay(600)
            requireNavigator().open(
                destination = AppDestination.Login(),
                clearBackStack = true,
                addToBackStack = false,
                transition = AppNavigationTransition.None,
            )
        }
    }

}

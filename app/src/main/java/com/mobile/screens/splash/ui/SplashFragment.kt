package com.mobile.screens.splash.ui

import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mobile.base.BaseFragmentBinding
import com.mobile.databinding.FragmentSplashBinding
import com.mobile.navigation.AppDestination
import com.mobile.navigation.AppNavigationTransition
import com.mobile.navigation.requireNavigator

class SplashFragment :
    BaseFragmentBinding<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var hasScheduledNavigation = false

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) {
        binding.logoCluster.apply {
            alpha = 0f
            translationY = resources.displayMetrics.density * 12f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(420L)
                .start()
        }
    }

    override fun initListener() = Unit

    override fun initObserve() = Unit

    override fun onResume() {
        super.onResume()
        if (hasScheduledNavigation) {
            return
        }
        hasScheduledNavigation = true
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1_000)
            requireNavigator().open(
                destination = AppDestination.Onboarding,
                clearBackStack = true,
                addToBackStack = false,
                transition = AppNavigationTransition.Forward,
            )
        }
    }

}

package com.mobile.screens.onboarding

import android.view.View
import com.mobile.base.BaseFragmentBinding
import com.mobile.databinding.FragmentOnboardingBinding
import com.mobile.navigation.AppDestination
import com.mobile.navigation.AppNavigationTransition
import com.mobile.navigation.requireNavigator

class OnboardingFragment :
    BaseFragmentBinding<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) {
        binding.onboardingContent.apply {
            alpha = 0f
            translationY = resources.displayMetrics.density * 10f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(360L)
                .start()
        }
    }

    override fun initListener() {
        binding.btnSkip.setOnClickListener { openLogin() }
        binding.btnContinue.setOnClickListener { openLogin() }
    }

    override fun initObserve() = Unit

    private fun openLogin() {
        requireNavigator().open(
            destination = AppDestination.Login(),
            clearBackStack = true,
            addToBackStack = false,
            transition = AppNavigationTransition.Forward,
        )
    }
}

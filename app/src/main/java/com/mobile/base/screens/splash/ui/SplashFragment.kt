package com.mobile.base.screens.splash.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mobile.base.R
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentSplashBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.navigation.AppNavigationTransition
import com.mobile.base.navigation.requireNavigator
import java.util.Base64

class SplashFragment :
    BaseFragmentBinding<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var hasScheduledNavigation = false

    override fun useBaseFadeThrough() = false

    override fun initView(view: View) {
//        mapBackground()
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
            delay(600)
            requireNavigator().open(
                destination = AppDestination.Login(),
                clearBackStack = true,
                addToBackStack = false,
                transition = AppNavigationTransition.None,
            )
        }
    }

    private fun mapBackground() {
        val bgLogin = storage.getBackgroundLogin()
        val bgBitmap: Bitmap? = if (bgLogin.isNotEmpty()) {
            prepareBackground(bgLogin)
        } else {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_logo_app)
            if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                drawable?.toBitmap()
            }
        }

        bgBitmap?.let(binding.ivBackground::setImageBitmap)
    }

    @SuppressLint("NewApi")
    private fun prepareBackground(bgBase64: String): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decoded = Base64.getDecoder().decode(bgBase64)
            BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        } else {
            val decoded = android.util.Base64.decode(bgBase64, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        }
    }
}

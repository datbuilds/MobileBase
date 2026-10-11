package com.mobile.base.screens.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mobile.base.R
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentHomeBinding

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private var coachMarkOverlay: FlowCoachMarkOverlay? = null

    override fun initView(view: View) {
        (activity as? MainActivity)?.startSessionTimer()
        binding.homeRoot.postDelayed({ showFirstRunCoachMarks() }, 520L)
    }

    override fun initListener() {
        binding.btnLogout.setOnClickListener { logout() }
    }

    override fun onDestroyView() {
        coachMarkOverlay?.let { overlay ->
            (overlay.parent as? ViewGroup)?.removeView(overlay)
        }
        coachMarkOverlay = null
        super.onDestroyView()
    }

    override fun initObserve() = Unit

    private fun showFirstRunCoachMarks() {
        if (!isAdded || view == null || coachMarkOverlay != null) return
        val prefs = requireContext().getSharedPreferences(HOME_COACH_PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(HOME_COACH_SEEN_KEY, false)) return

        val steps = listOf(
            CoachMarkStep(
                anchor = binding.tvHomeTitle,
                title = getString(R.string.home_coach_welcome_title),
                body = getString(R.string.home_coach_welcome_body),
            ),
            CoachMarkStep(
                anchor = binding.btnLogout,
                title = getString(R.string.home_coach_logout_title),
                body = getString(R.string.home_coach_logout_body),
            ),
        )
        val overlay = FlowCoachMarkOverlay(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            elevation = dp(40).toFloat()
        }
        coachMarkOverlay = overlay
        binding.homeRoot.addView(overlay)
        overlay.show(steps) {
            prefs.edit().putBoolean(HOME_COACH_SEEN_KEY, true).apply()
            (overlay.parent as? ViewGroup)?.removeView(overlay)
            if (coachMarkOverlay === overlay) coachMarkOverlay = null
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }

    private companion object {
        private const val HOME_COACH_PREFS = "mobile_base_home_coach"
        private const val HOME_COACH_SEEN_KEY = "home_coach_seen"
    }
}

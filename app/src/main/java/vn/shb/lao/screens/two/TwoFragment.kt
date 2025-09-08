package vn.shb.lao.screens.two

import android.annotation.SuppressLint
import android.view.View
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.databinding.FragmentTwoBinding

class TwoFragment :
    BaseFragmentBinding<FragmentTwoBinding>(FragmentTwoBinding::inflate) {

    companion object {
        fun newInstance() = TwoFragment()
    }

    override fun initView(view: View) {
        with(binding) {
            toolbar.setTitle("2Fragment", isShow = false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(binding) {
            binding.rootView.setOnTouchListener { _, _ ->
                hideSoftKeyboard(0)
                false
            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {

        }
    }

    /**
     * Reset UI
     */
    fun resetUI() {
        with(binding) {
            resetSegments()
        }
    }

    private fun resetSegments() {
    }
}
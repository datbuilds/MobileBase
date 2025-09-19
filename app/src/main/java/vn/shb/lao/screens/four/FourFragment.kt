package vn.shb.lao.screens.four

import android.annotation.SuppressLint
import android.view.View
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.databinding.FragmentFourBinding

class FourFragment :
    BaseFragmentBinding<FragmentFourBinding>(FragmentFourBinding::inflate) {
    companion object {
        fun newInstance() = FourFragment()
    }

    override fun initView(view: View) {
        binding.toolbar.setTitle("4Fragment", false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(binding) {
            rootView.setOnTouchListener { _, _ ->
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
package vn.shb.lao.screens.three

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.databinding.FragmentThreeBinding

class ThreeFragment :
    BaseFragmentBinding<FragmentThreeBinding>(FragmentThreeBinding::inflate) {

    companion object {
        fun newInstance() = ThreeFragment()
        private const val ITEMS_PER_PAGE = 20
    }

    private val inputNumber = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)

    override fun initView(view: View) {
        toolbar()
    }

    private fun toolbar() {
        binding.toolbar.setTitle("3Fragment", isShow = false)
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
        launchRepeatOnLifecycle {}
    }

    /**
     * Reset UI
     */
    fun resetUI() {
    }
}
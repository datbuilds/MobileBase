package vn.shb.cam.base.dialog

import android.view.View
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogResponseStatusBinding
import vn.shb.cam.utils.extensions.fromHtml

class DialogWarningAccessibilityPermission(private val build: Build) :
    BaseBottomDialogBinding<DialogResponseStatusBinding>
        (DialogResponseStatusBinding::inflate) {

    companion object {
        const val TAG = "DialogWarningAccessibilityPermission"
    }

    override fun initView(view: View) {
        with(binding) {
            tvTitle.text = requireContext().getString(vn.shb.cam.localization.R.string.title_noti)
            tvContentError.fromHtml(getString(vn.shb.cam.localization.R.string.warning_accessibility_permission))
        }
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            finish()
        }

        binding.btnClose.setOnSingleClickListener {
            finish()
        }
    }

    private fun finish() {
        build.onFinish()
        dismiss()
    }

    override fun initObserve() {}

    class Build(val onFinish: () -> Unit) {
        fun build() = DialogWarningAccessibilityPermission(this)
    }
}
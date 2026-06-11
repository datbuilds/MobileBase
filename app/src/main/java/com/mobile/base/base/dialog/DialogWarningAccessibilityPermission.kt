package com.mobile.base.base.dialog

import android.view.View
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.R
import com.mobile.base.base.BaseBottomDialogBinding
import com.mobile.base.databinding.DialogResponseStatusBinding
import com.mobile.base.utils.extensions.fromHtml

class DialogWarningAccessibilityPermission(private val build: Build) :
    BaseBottomDialogBinding<DialogResponseStatusBinding>
        (DialogResponseStatusBinding::inflate) {

    companion object {
        const val TAG = "DialogWarningAccessibilityPermission"
    }

    override fun initView(view: View) {
        with(binding) {
            tvTitle.text = requireContext().getString(com.mobile.base.localization.R.string.title_noti)
            tvContentError.fromHtml(getString(com.mobile.base.localization.R.string.warning_accessibility_permission))
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
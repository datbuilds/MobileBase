package com.mobile.base.dialog

import android.view.View
import com.mobile.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.BaseBottomDialogBinding
import com.mobile.databinding.DialogDeviceRootBinding

class DialogWarningDeviceRoot(private val build: Build) :
    BaseBottomDialogBinding<DialogDeviceRootBinding>(DialogDeviceRootBinding::inflate) {
    override fun initView(view: View) {

    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            dismiss()
        }
        binding.btnDeviceRootClose.setOnSingleClickListener {
            build.actionDeviceRoot.invoke()
            dismiss()
        }
    }

    override fun initObserve() {}

    class Build(val actionDeviceRoot: () -> Unit) {
        fun build() = DialogWarningDeviceRoot(this)
    }

    companion object {
        const val TAG = "DialogDeviceRoot"
    }
}
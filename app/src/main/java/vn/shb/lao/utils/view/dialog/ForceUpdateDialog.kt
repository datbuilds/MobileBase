package vn.shb.lao.utils.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.databinding.DialogForceUpdateBinding

class ForceUpdateDialog(
    context: Context,
    private val onUpdateClick: () -> Unit
) : AlertDialog(context) {

    private lateinit var binding: DialogForceUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogForceUpdateBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        initListener()
    }

    private fun initListener() {
        binding.btnUpdate.setOnSingleClickListener {
            onUpdateClick()
        }
    }
}

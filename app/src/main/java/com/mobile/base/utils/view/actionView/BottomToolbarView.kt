package com.mobile.base.utils.view.actionView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.R
import com.mobile.base.utils.extensions.inflate

class BottomToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var tvTitle: AppCompatTextView
    private var ivBack: AppCompatImageView

    private var listener: OnBsToolbarListener? = null

    fun setListener(listener: OnBsToolbarListener) {
        this.listener = listener
    }

    init {
        val itemLayout = inflate(R.layout.bottom_toolbar_view, true) as LinearLayoutCompat

        ivBack = itemLayout.findViewById(R.id.ivBack)
        tvTitle = itemLayout.findViewById(R.id.tvTitle)

        ivBack.setOnSingleClickListener {
            listener?.onPreviousClick()
        }
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }
}

interface OnBsToolbarListener {
    fun onPreviousClick()
//    fun onNextClick()
}
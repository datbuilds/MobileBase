package vn.shb.lao.utils.extensions.common.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.ViewCompat

class WindowInsetsLinearLayout : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ViewCompat.requestApplyInsets(this)
    }
}
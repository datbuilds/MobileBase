package vn.shb.lao.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.shb.lao.R

class AppTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyTextView,
            0, 0
        ).apply {
            typeface = try {
                when (getInt(R.styleable.MyTextView_fontWeight, 0)) {
                    1 -> FontManager.medium
                    2 -> FontManager.bold
                    else -> FontManager.regular
                }
            } finally {
                recycle()
            }
        }
    }
}
package vn.shb.lao.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.shb.core.core.delivery.ReasonDescription.LAO
import vn.shb.lao.R
import vn.shb.lao.utils.widgets.LocaleHelper

class MyTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val isLao = LocaleHelper.getCurrentLanguage(context) == LAO
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyTextView,
            0, 0
        ).apply {
            typeface = try {
                when (getInt(R.styleable.MyTextView_customFontWeight, 0)) {
                    1 -> FontManager.medium
                    2 -> FontManager.bold
                    3 -> FontManager.semi_bold
                    else -> FontManager.regular
                }
            } finally {
                recycle()
            }
        }
    }
}
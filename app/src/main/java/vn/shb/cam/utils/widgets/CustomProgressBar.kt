package vn.shb.cam.utils.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.ProgressBar
import vn.shb.cam.R

class CustomProgressBar : ProgressBar {
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (!isInEditMode) {
            if (attrs != null) {
                val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
                val color = styledAttributes.getColor(R.styleable.CustomProgressBar_foreground_color, 0)
                styledAttributes.recycle()
                if (color != 0) {
                    indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                }
            }
        }
    }
}

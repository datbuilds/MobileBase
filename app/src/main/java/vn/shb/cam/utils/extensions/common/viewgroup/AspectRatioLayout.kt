package vn.shb.cam.utils.extensions.common.viewgroup

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import vn.shb.cam.R
import kotlin.math.roundToInt

class AspectRatioLayout : ConstraintLayout {
    private var widthRatio = 0f
    private var heightRatio = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout)
        widthRatio = a.getFloat(R.styleable.AspectRatioLayout_arl_widthRatio, 1f)
        heightRatio = a.getFloat(R.styleable.AspectRatioLayout_arl_heightRatio, 1f)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var newWidthMeasureSpec = widthMeasureSpec
        var newHeightMeasureSpec = heightMeasureSpec
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (heightRatio / widthRatio * width).roundToInt()
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        } else if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width = (widthRatio / heightRatio * height).roundToInt()
            newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        }
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }
}
package vn.shb.cam.utils.view.dot

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import vn.shb.cam.R

@SuppressLint("CustomViewStyleable")
class CustomIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr), OnTouchListener {
    private var lineWidth = 0
    private var lineWidthSelected = 0
    private var lineHeight = 0
    private var lineMargin = 0
    private var lineWidthWithMargin = 0
    private var lineWidthSelectedWithMargin = 0
    private var tooltipWidth = 0
    private var tooltipHeight = 0
    private var selectedLineDrawableResource = 0
    private var unselectedLineDrawableResource = 0
    private var adapter: RecyclerView.Adapter<*>? = null
    private var linearLayout: LinearLayout? = null
    private var tooltipView: RelativeLayout? = null
    private var tooltipViewImage: AppCompatImageView? = null
    private var selectedPosition = -1
    private var expandAnimator: ValueAnimator? = null
    private var collapseAnimator: ValueAnimator? = null
    private var drawableList: Array<Drawable>? = null

    init {
        this.clipChildren = false
        this.clipToPadding = false
        this.isClickable = true
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TooltipIndicator)
        try {
            lineWidth = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_lineWidth,
                dpToPx(16).toFloat()
            ).toInt()
            lineHeight = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_lineHeight,
                dpToPx(6).toFloat()
            ).toInt()
            lineWidthSelected = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_lineWidthSelected,
                dpToPx(32).toFloat()
            ).toInt()
            lineMargin = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_lineMargin,
                dpToPx(4).toFloat()
            ).toInt()
            lineWidthWithMargin = lineWidth + lineMargin * 2
            lineWidthSelectedWithMargin = lineWidthSelected + lineMargin * 2
            tooltipWidth = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_tooltipWidth,
                dpToPx(100).toFloat()
            ).toInt()
            tooltipHeight = typedArray.getDimension(
                R.styleable.TooltipIndicator_ti_tooltipHeight,
                dpToPx(180).toFloat()
            ).toInt()
            selectedLineDrawableResource = typedArray.getResourceId(
                R.styleable.TooltipIndicator_ti_selectedLineDrawable,
                R.drawable.indicator_rounded_line_selected
            )
            unselectedLineDrawableResource = typedArray.getResourceId(
                R.styleable.TooltipIndicator_ti_unselectedLineDrawable,
                R.drawable.indicator_rounded_line_unselected
            )
        } finally {
            typedArray.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupViewPager(viewPager: ViewPager2) {
        adapter = viewPager.adapter
        if (adapter == null) {
            throw NullPointerException("ViewPager's adapter cannot be null.")
        }
        removeAllViews()
        initIndicatorLines()
        initToolTipView()
        selectPage(1)

        setOnTouchListener(this)
    }

    fun setToolTipDrawables(drawableList: List<Drawable>) {
        if (adapter == null) {
            throw NullPointerException("ViewPager's adapter cannot be null.")
        }
        this.drawableList = drawableList.toTypedArray()
    }

    //region Initialize ToolTip and Lines
    private fun initToolTipView() {
        val layoutParams = LayoutParams(tooltipWidth, tooltipHeight)
        layoutParams.topMargin = -(layoutParams.height + dpToPx(8))
        tooltipView = RelativeLayout(context)
        tooltipView?.layoutParams = layoutParams
        tooltipView?.setBackgroundResource(R.drawable.indicator_rounded_line_selected)
        tooltipView?.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
        tooltipViewImage = AppCompatImageView(context)
        tooltipViewImage?.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        tooltipView?.scaleX = 0f
        tooltipView?.scaleY = 0f
        tooltipView?.alpha = 0f
        tooltipView?.translationY = (layoutParams.height / 2).toFloat()
        tooltipView?.addView(tooltipViewImage)
        this.addView(tooltipView)
    }

    private fun initIndicatorLines() {
        linearLayout = LinearLayout(context)
        linearLayout?.setVerticalGravity(LinearLayout.HORIZONTAL)
        val linesCount = adapter!!.itemCount
        for (i in 0 until linesCount) {
            val lineView = View(context)
            lineView.setBackgroundResource(unselectedLineDrawableResource)
            val layoutParams = LinearLayout.LayoutParams(lineWidth, lineHeight)
            layoutParams.leftMargin = lineMargin
            layoutParams.rightMargin = lineMargin
            lineView.layoutParams = layoutParams
            linearLayout?.addView(lineView)
        }
        this.addView(linearLayout)
    }

    //endregion
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x.toInt()
        if (drawableList == null || drawableList?.isEmpty() == true) {
            return true
        }
        var toolTipX = 0.coerceAtLeast(x)
        toolTipX = toolTipX.coerceAtMost(measuredWidth)
        tooltipView?.x = (toolTipX - tooltipView!!.measuredWidth / 2).toFloat()
        var pos: Int
        val totalWidthBefore = selectedPosition * lineWidthWithMargin
        pos = if (toolTipX >= totalWidthBefore + lineWidthSelectedWithMargin) {
            selectedPosition + 1 + (toolTipX - (totalWidthBefore + lineWidthSelectedWithMargin)) / lineWidthWithMargin
        } else if (toolTipX > totalWidthBefore) {
            selectedPosition
        } else {
            toolTipX / lineWidthWithMargin
        }
        pos = pos.coerceAtMost(drawableList!!.size - 1)
        if (tooltipViewImage?.background !== drawableList!![pos]) {
            tooltipViewImage?.setBackgroundDrawable(drawableList!![pos])
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> tooltipView?.animate()?.alpha(1f)?.scaleX(1f)?.scaleY(1f)
                ?.translationY(1f)?.setInterpolator(OvershootInterpolator())?.start()

            MotionEvent.ACTION_UP -> tooltipView?.animate()?.alpha(0f)?.scaleX(0f)?.scaleY(0f)
                ?.translationY((tooltipView!!.measuredHeight / 2).toFloat())
                ?.setInterpolator(LinearInterpolator())?.start()
        }
        return true
    }

    fun selectPage(position: Int) {
        if (selectedPosition != -1) {
            collapseView(linearLayout!!.getChildAt(selectedPosition))
        }
        selectedPosition = position
        expandView(linearLayout!!.getChildAt(selectedPosition))
    }

    private fun expandView(selectedView: View) {
        selectedView.setBackgroundResource(selectedLineDrawableResource)
        if (expandAnimator != null) {
            expandAnimator!!.end()
        }
        expandAnimator = ValueAnimator.ofObject(IntEvaluator(), lineWidth, lineWidthSelected)
        expandAnimator?.interpolator = DecelerateInterpolator()
        expandAnimator?.addUpdateListener { animation ->
            val layoutParams = selectedView.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = animation.animatedValue as Int
            selectedView.layoutParams = layoutParams
        }
        expandAnimator?.duration = 200
        expandAnimator?.start()
    }

    private fun collapseView(selectedView: View) {
        selectedView.setBackgroundResource(unselectedLineDrawableResource)
        if (collapseAnimator != null) {
            collapseAnimator?.end()
        }
        collapseAnimator = ValueAnimator.ofObject(IntEvaluator(), lineWidthSelected, lineWidth)
        collapseAnimator?.interpolator = DecelerateInterpolator()
        collapseAnimator?.addUpdateListener { animation ->
            val layoutParams = selectedView.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = animation.animatedValue as Int
            selectedView.layoutParams = layoutParams
        }
        collapseAnimator?.duration = 500
        collapseAnimator?.start()
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    companion object {
        const val TAG = "CustomIndicator"
    }
}
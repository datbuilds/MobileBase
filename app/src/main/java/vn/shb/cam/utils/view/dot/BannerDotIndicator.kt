package vn.shb.cam.utils.view.dot

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

class BannerDotIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Dot unselected: circle with stroke
    private var dotRadius = dpToPx(3.5f)
    private var dotStrokeWidth = dpToPx(1.2f)

    // Dot selected: capsule (rounded rect) fill white
    private var selectedWidth = dpToPx(20f)
    private var selectedHeight = dpToPx(8f)

    // Spacing between dots
    private var dotSpacing = dpToPx(8f)

    // Colors
    private var dotStrokeColor = 0x80FFFFFF.toInt() // white with 50% alpha
    private var selectedColor = 0xFFFFFFFF.toInt()  // solid white

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dotStrokeWidth
        color = dotStrokeColor
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = selectedColor
    }

    private var itemCount = 0
    private var selectedPosition = 0
    private var animatedWidth = selectedWidth
    private var animator: ValueAnimator? = null

    private val rectF = RectF()

    fun setItemCount(count: Int) {
        itemCount = count
        requestLayout()
        invalidate()
    }

    fun setupWithRecyclerView(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter
            ?: throw NullPointerException("RecyclerView adapter cannot be null")
        itemCount = adapter.itemCount
        requestLayout()
        invalidate()
    }

    fun selectPage(position: Int) {
        if (position == selectedPosition && animatedWidth == selectedWidth) return
        selectedPosition = position.coerceIn(0, (itemCount - 1).coerceAtLeast(0))

        animator?.cancel()
        animator = ValueAnimator.ofFloat(dotRadius * 2, selectedWidth).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                animatedWidth = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (itemCount == 0) {
            setMeasuredDimension(0, 0)
            return
        }

        // Total width: (itemCount - 1) unselected circles + 1 selected capsule + spacing
        val unselectedDiameter = dotRadius * 2
        val totalWidth = (itemCount - 1) * unselectedDiameter +
                selectedWidth +
                (itemCount - 1) * dotSpacing +
                dotStrokeWidth * 2 // extra padding for stroke

        val totalHeight = selectedHeight.coerceAtLeast(unselectedDiameter + dotStrokeWidth * 2)

        setMeasuredDimension(
            resolveSize(totalWidth.toInt() + paddingLeft + paddingRight, widthMeasureSpec),
            resolveSize(totalHeight.toInt() + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemCount == 0) return

        val centerY = height / 2f
        val unselectedDiameter = dotRadius * 2
        var currentX = paddingLeft + dotStrokeWidth

        for (i in 0 until itemCount) {
            if (i == selectedPosition) {
                // Draw selected: capsule (rounded rect) filled white
                val capsuleHeight = selectedHeight
                val cornerRadius = capsuleHeight / 2f

                rectF.set(
                    currentX,
                    centerY - capsuleHeight / 2f,
                    currentX + animatedWidth,
                    centerY + capsuleHeight / 2f
                )
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint)
                currentX += animatedWidth + dotSpacing
            } else {
                // Draw unselected: circle with stroke only
                val cx = currentX + dotRadius
                val cy = centerY
                canvas.drawCircle(cx, cy, dotRadius - dotStrokeWidth / 2f, strokePaint)
                currentX += unselectedDiameter + dotSpacing
            }
        }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }
}

package com.mobile.base.screens.home

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.mobile.base.R
import kotlin.math.min

data class CoachMarkStep(
    val anchor: View,
    val title: String,
    val body: String
)

@Suppress("DEPRECATION")
class FlowCoachMarkOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B0000000")
    }
    private val cutoutPath = Path().apply {
        fillType = Path.FillType.EVEN_ODD
    }
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_card)
        setShadowLayer(dp(18).toFloat(), 0f, dp(8).toFloat(), Color.parseColor("#3D172033"))
    }
    private val cardStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1).toFloat()
        color = Color.parseColor("#40FFFFFF")
    }
    private val focusStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(2).toFloat()
        color = ContextCompat.getColor(context, R.color.coach_mark_accent_soft)
    }
    private val focusGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(6).toFloat()
        color = Color.parseColor("#55F97316")
    }
    private val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_accent)
    }
    private val skipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_accent_soft)
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_card)
    }
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_text)
        textSize = sp(17).toFloat()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_muted)
        textSize = sp(14).toFloat()
    }
    private val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_accent_dark)
        textSize = sp(12).toFloat()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.RIGHT
    }
    private val actionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = sp(14).toFloat()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val skipTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.coach_mark_accent_dark)
        textSize = sp(14).toFloat()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val rootRect = Rect()
    private val anchorRect = Rect()
    private val focusRect = RectF()
    private val animatedFocusRect = RectF()
    private val cardRect = RectF()
    private val nextRect = RectF()
    private val skipRect = RectF()
    private val arrowPath = Path()
    private var steps: List<CoachMarkStep> = emptyList()
    private var currentIndex = 0
    private var onFinished: (() -> Unit)? = null
    private var revealAnimator: ValueAnimator? = null
    private var revealProgress = 1f
    private val motionInterpolator = DecelerateInterpolator(1.6f)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    fun show(
        steps: List<CoachMarkStep>,
        onFinished: () -> Unit
    ) {
        this.steps = steps.filter { step -> step.anchor.isShown && step.anchor.width > 0 && step.anchor.height > 0 }
        this.onFinished = onFinished
        currentIndex = 0
        if (this.steps.isEmpty()) {
            finish()
            return
        }
        alpha = 0f
        animate().alpha(1f).setDuration(180L).setInterpolator(motionInterpolator).start()
        announceForAccessibility(this.steps.first().title)
        startStepReveal()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val step = steps.getOrNull(currentIndex) ?: return
        val anchorReady = resolveFocusRect(step.anchor)
        if (!anchorReady) return
        updateAnimatedFocusRect()

        cutoutPath.reset()
        cutoutPath.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        val focusRadius = dp(12) + (dp(10) * revealProgress)
        cutoutPath.addRoundRect(animatedFocusRect, focusRadius, focusRadius, Path.Direction.CW)
        canvas.drawPath(cutoutPath, dimPaint)
        drawAnimatedFocus(canvas, focusRadius)

        val cardWidth = min(width - dp(32), dp(328)).toFloat()
        val textWidth = (cardWidth - dp(40)).toInt()
        val titleLayout = staticLayout(step.title, titlePaint, textWidth)
        val bodyLayout = staticLayout(step.body, bodyPaint, textWidth)
        val cardHeight = (
            dp(20) +
                titleLayout.height +
                dp(8) +
                bodyLayout.height +
                dp(20) +
                dp(40) +
                dp(16)
            ).toFloat()
        layoutCard(cardWidth, cardHeight)
        drawAnimatedCard(canvas, titleLayout, bodyLayout)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            when {
                nextRect.contains(event.x, event.y) -> showNext()
                skipRect.contains(event.x, event.y) -> finish()
            }
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun showNext() {
        if (currentIndex >= steps.lastIndex) {
            finish()
            return
        }
        currentIndex += 1
        announceForAccessibility(steps[currentIndex].title)
        startStepReveal()
    }

    private fun finish() {
        revealAnimator?.cancel()
        animate().alpha(0f).setDuration(150L).setInterpolator(motionInterpolator).withEndAction {
            onFinished?.invoke()
        }.start()
    }

    private fun startStepReveal() {
        revealAnimator?.cancel()
        revealProgress = 0f
        revealAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 280L
            interpolator = motionInterpolator
            addUpdateListener { animator ->
                revealProgress = animator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun resolveFocusRect(anchor: View): Boolean {
        if (!getGlobalVisibleRect(rootRect) || !anchor.getGlobalVisibleRect(anchorRect)) return false
        focusRect.set(
            (anchorRect.left - rootRect.left - dp(8)).toFloat(),
            (anchorRect.top - rootRect.top - dp(8)).toFloat(),
            (anchorRect.right - rootRect.left + dp(8)).toFloat(),
            (anchorRect.bottom - rootRect.top + dp(8)).toFloat()
        )
        focusRect.left = focusRect.left.coerceAtLeast(dp(8).toFloat())
        focusRect.top = focusRect.top.coerceAtLeast(dp(8).toFloat())
        focusRect.right = focusRect.right.coerceAtMost(width - dp(8).toFloat())
        focusRect.bottom = focusRect.bottom.coerceAtMost(height - dp(8).toFloat())
        return focusRect.width() > dp(24) && focusRect.height() > dp(24)
    }

    private fun updateAnimatedFocusRect() {
        val centerX = focusRect.centerX()
        val centerY = focusRect.centerY()
        val startHalfSize = dp(12).toFloat()
        animatedFocusRect.set(
            lerp(centerX - startHalfSize, focusRect.left, revealProgress),
            lerp(centerY - startHalfSize, focusRect.top, revealProgress),
            lerp(centerX + startHalfSize, focusRect.right, revealProgress),
            lerp(centerY + startHalfSize, focusRect.bottom, revealProgress)
        )
    }

    private fun drawAnimatedFocus(canvas: Canvas, radius: Float) {
        focusGlowPaint.alpha = (0x55 * revealProgress).toInt()
        focusStrokePaint.alpha = (0xFF * revealProgress).toInt()
        canvas.drawRoundRect(animatedFocusRect, radius, radius, focusGlowPaint)
        canvas.drawRoundRect(animatedFocusRect, radius, radius, focusStrokePaint)
        focusGlowPaint.alpha = 0x55
        focusStrokePaint.alpha = 0xFF
    }

    private fun layoutCard(cardWidth: Float, cardHeight: Float) {
        val gap = dp(18).toFloat()
        val horizontalMargin = dp(16).toFloat()
        val preferredLeft = (focusRect.centerX() - cardWidth / 2)
            .coerceIn(horizontalMargin, width - horizontalMargin - cardWidth)
        val belowTop = focusRect.bottom + gap
        val aboveTop = focusRect.top - gap - cardHeight
        val top = when {
            belowTop + cardHeight <= height - dp(20) -> belowTop
            aboveTop >= dp(20) -> aboveTop
            else -> (height - cardHeight) / 2f
        }
        cardRect.set(preferredLeft, top, preferredLeft + cardWidth, top + cardHeight)
    }

    private fun drawAnimatedCard(
        canvas: Canvas,
        titleLayout: StaticLayout,
        bodyLayout: StaticLayout
    ) {
        val cardAlpha = (255 * revealProgress).toInt().coerceIn(0, 255)
        val cardScale = 0.96f + (0.04f * revealProgress)
        val cardLift = dp(12).toFloat() * (1f - revealProgress)
        canvas.saveLayerAlpha(0f, 0f, width.toFloat(), height.toFloat(), cardAlpha)
        canvas.translate(0f, cardLift)
        canvas.scale(cardScale, cardScale, cardRect.centerX(), cardRect.centerY())
        drawArrow(canvas)
        drawCard(canvas, titleLayout, bodyLayout)
        canvas.restore()
    }

    private fun drawArrow(canvas: Canvas) {
        val arrowCenterX = focusRect.centerX().coerceIn(cardRect.left + dp(28), cardRect.right - dp(28))
        val arrowSize = dp(10).toFloat()
        val pointsDown = cardRect.top > focusRect.bottom
        arrowPath.reset()
        if (pointsDown) {
            arrowPath.moveTo(arrowCenterX - arrowSize, cardRect.top)
            arrowPath.lineTo(arrowCenterX + arrowSize, cardRect.top)
            arrowPath.lineTo(arrowCenterX, cardRect.top - arrowSize)
        } else {
            arrowPath.moveTo(arrowCenterX - arrowSize, cardRect.bottom)
            arrowPath.lineTo(arrowCenterX + arrowSize, cardRect.bottom)
            arrowPath.lineTo(arrowCenterX, cardRect.bottom + arrowSize)
        }
        arrowPath.close()
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun drawCard(
        canvas: Canvas,
        titleLayout: StaticLayout,
        bodyLayout: StaticLayout
    ) {
        canvas.drawRoundRect(cardRect, dp(20).toFloat(), dp(20).toFloat(), cardPaint)
        canvas.drawRoundRect(cardRect, dp(20).toFloat(), dp(20).toFloat(), cardStrokePaint)

        val contentLeft = cardRect.left + dp(20)
        var y = cardRect.top + dp(20)
        val stepText = "${currentIndex + 1}/${steps.size}"
        canvas.drawText(stepText, cardRect.right - dp(20), y + dp(12), metaPaint)

        canvas.save()
        canvas.translate(contentLeft, y)
        titleLayout.draw(canvas)
        canvas.restore()
        y += titleLayout.height + dp(8)

        canvas.save()
        canvas.translate(contentLeft, y)
        bodyLayout.draw(canvas)
        canvas.restore()

        val buttonTop = cardRect.bottom - dp(56)
        skipRect.set(contentLeft, buttonTop, contentLeft + dp(104), buttonTop + dp(40))
        nextRect.set(cardRect.right - dp(128), buttonTop, cardRect.right - dp(20), buttonTop + dp(40))
        canvas.drawRoundRect(skipRect, dp(20).toFloat(), dp(20).toFloat(), skipPaint)
        canvas.drawRoundRect(nextRect, dp(20).toFloat(), dp(20).toFloat(), accentPaint)

        canvas.drawText(
            context.getString(R.string.home_coach_skip),
            skipRect.centerX(),
            skipRect.centerY() + textCenterOffset(skipTextPaint),
            skipTextPaint
        )
        val nextText = if (currentIndex == steps.lastIndex) {
            context.getString(R.string.home_coach_finish)
        } else {
            context.getString(R.string.home_coach_next)
        }
        canvas.drawText(
            nextText,
            nextRect.centerX(),
            nextRect.centerY() + textCenterOffset(actionPaint),
            actionPaint
        )
    }

    @Suppress("DEPRECATION")
    private fun staticLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.08f, 0f, false)
    }

    private fun textCenterOffset(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return -(metrics.ascent + metrics.descent) / 2f
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + ((end - start) * progress)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun sp(value: Int): Int {
        return (value * resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }
}

package vn.shb.lao.utils.extensions

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import kotlin.math.roundToInt

fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE

}

fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.fadeIn(durationMillis: Long = 250) {
    this.startAnimation(AlphaAnimation(0F, 1F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

fun View.fadeOut(durationMillis: Long = 250) {
    this.startAnimation(AlphaAnimation(1F, 0F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

fun View.goneWhenNull(value: Any?) {
    this.visibleWhenTrue(value != null)
    fadeOut()
}

fun View.visibleWhenTrue(value: Boolean) {
    this.visibility = if (value) View.VISIBLE else View.GONE
}

fun View.goneWhenTrue(value: Boolean) {
    this.visibility = if (value) View.GONE else View.VISIBLE
}

fun View.invisibleWhenTrue(value: Boolean) {
    this.visibility = if (value) View.INVISIBLE else View.VISIBLE
}

fun View.pxToDp(value: Int): Int =
    (value.toFloat() / context.resources.displayMetrics.density).roundToInt()

fun View.activated() {
    isActivated = true
}

fun View.resetActivated() {
    isActivated = false
}

/**
 * Expand and collapse animation
 */

fun View.expand(duration: Long = 300L) {
    measure(
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.UNSPECIFIED
    )
    val targetHeight = measuredHeight
    layoutParams.height = 0
    visibility = View.VISIBLE

    val animator = ValueAnimator.ofInt(0, targetHeight)
    animator.duration = duration
    animator.interpolator = AccelerateDecelerateInterpolator()

    animator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.height = value
        requestLayout()
    }
    animator.start()
}

fun View.collapse(duration: Long = 300L) {
    val initialHeight = measuredHeight

    val animator = ValueAnimator.ofInt(initialHeight, 0)
    animator.duration = duration
    animator.interpolator = AccelerateDecelerateInterpolator()

    animator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.height = value
        requestLayout()
        if (value == 0) visibility = View.GONE
    }
    animator.start()
}

/************************************************
 *                 Insets Section               *
 ************************************************/

/*******************
 * Utility methods *
 *******************/

fun View.applySystemBarPaddingInsets() {
    this.doOnApplyWindowInsets { view, insets, padding, _ ->
        view.updatePadding(top = padding.top + insets.systemWindowInsetTop)
    }
}

fun View.applyNavigationBarPaddingInsets() {
    this.doOnApplyWindowInsets { view, insets, padding, _ ->
        view.updatePadding(bottom = padding.bottom + insets.systemWindowInsetBottom)
    }
}

fun View.applyNavigationAndBottomNavigationViewMarginInsets() {
    this.doOnApplyWindowInsets { view, insets, _, margin ->
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(
                margin.left,
                margin.top,
                margin.right,
                margin.bottom + insets.systemWindowInsetBottom
            )
        }
    }
}

fun View.applyVerticalInsets() {
    this.doOnApplyWindowInsets { view, windowInsets, initialPadding, _ ->
        view.updatePadding(
            top = initialPadding.top + windowInsets.systemWindowInsetTop,
            bottom = initialPadding.bottom + windowInsets.systemWindowInsetBottom
        )
    }
}

/*********************
 * Implementation(s) *
 *********************/

fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding, InitialMargin) -> Unit) {
    // Create a snapshot of the view's padding/margin state
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    setOnApplyWindowInsetsListener { v, insets ->
        insets.consumeSystemWindowInsets()
        f(v, insets, initialPadding, initialMargin)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

class InitialPadding(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

class InitialMargin(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

private fun recordInitialPaddingForView(view: View) =
    InitialPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun recordInitialMarginForView(view: View) =
    InitialMargin(
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0,
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0,
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0,
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    )

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

//fun expand(v: View) {
//    val matchParentMeasureSpec =
//        View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
//    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
//    val targetHeight = v.measuredHeight
//
//    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
//    v.layoutParams.height = 1
//    v.visibility = View.VISIBLE
//    val a: Animation = object : Animation() {
//        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
//            v.layoutParams.height =
//                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
//            v.requestLayout()
//        }
//
//        override fun willChangeBounds(): Boolean {
//            return true
//        }
//    }
//
//    // Expansion speed of 1dp/ms
//    a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
//    v.startAnimation(a)
//}
//
//fun collapse(v: View) {
//    val initialHeight = v.measuredHeight
//    val a: Animation = object : Animation() {
//        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
//            if (interpolatedTime == 1f) {
//                v.visibility = View.GONE
//            } else {
//                v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
//                v.requestLayout()
//            }
//        }
//
//        override fun willChangeBounds(): Boolean {
//            return true
//        }
//    }
//
//    // Collapse speed of 1dp/ms
//    a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
//    v.startAnimation(a)
//}

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable =
    requireNotNull(ContextCompat.getDrawable(this, drawable))

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))


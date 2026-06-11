package com.mobile.base.utils.view.recyclerview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Created by SauCaCa on 8/1/2022.
 */
@SuppressLint("RestrictedApi")
class ExtendedFABOnScrollBehavior<V : View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    private var height: Int = 0
    private var width: Int = 0
    private var parentWidth: Int = 0
    private var currentState = STATE_SCROLLED_UP
    private var currentAnimator: ViewPropertyAnimator? = null

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            updateSnackbar(child, dependency)
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    private fun updateSnackbar(child: View, snackbarLayout: Snackbar.SnackbarLayout) {
        val params = snackbarLayout.layoutParams as? CoordinatorLayout.LayoutParams
        params?.run {
            anchorId = child.id
            anchorGravity = Gravity.TOP
            gravity = Gravity.TOP
            snackbarLayout.layoutParams = params
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        assureBottomViewVisibility(target, child)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        height = child.measuredHeight
        width = child.measuredWidth
        parentWidth = parent.width
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean =
        axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V,
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        @ViewCompat.NestedScrollType type: Int
    ) {
        if (currentState != STATE_SCROLLED_DOWN && dyConsumed > 0) {
            slideDown(child)
        } else if (currentState != STATE_SCROLLED_UP && dyConsumed < 0) {
            slideUp(child)
        }
    }

    private fun slideUp(child: V) {
        if (currentAnimator != null) {
            currentAnimator?.cancel()
            child.clearAnimation()
        }
        currentState = STATE_SCROLLED_UP
        if (child is ExtendedFloatingActionButton) {
            child.extend()
            animateChildToX(
                child,
                0,
                ENTER_ANIMATION_DURATION,
                AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
            )
        } else
            animateChildToY(
                child,
                0,
                ENTER_ANIMATION_DURATION,
                AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
            )
    }

    private fun slideDown(child: V) {
        if (currentAnimator != null) {
            currentAnimator?.cancel()
            child.clearAnimation()
        }
        currentState = STATE_SCROLLED_DOWN
        if (child is ExtendedFloatingActionButton) {
            child.shrink()

            (child.parent as? ViewGroup)?.width?.let { parentWidth ->
                val shrinkWidth = parentWidth
                    .minus(child.x)
                    .minus(child.height.times(2))
                    .minus(child.marginEnd)
                    .plus(child.elevation)
                    .toInt()

                animateChildToX(
                    child,
                    shrinkWidth,
                    EXIT_ANIMATION_DURATION,
                    AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
                )
            } ?: run {
                val lp = child.layoutParams as? CoordinatorLayout.LayoutParams
                lp?.anchorGravity = Gravity.BOTTOM or Gravity.END
                child.layoutParams = lp
            }
        } else
            animateChildToY(
                child,
                height,
                EXIT_ANIMATION_DURATION,
                AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
            )
    }

    private fun animateChildToY(
        child: V,
        targetY: Int,
        duration: Long,
        interpolator: TimeInterpolator
    ) {
        currentAnimator = child.animate()
            .translationY(targetY.toFloat())
            .setInterpolator(interpolator)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }
            })
    }

    private fun animateChildToX(
        child: V,
        targetX: Int,
        duration: Long,
        interpolator: TimeInterpolator
    ) {
        currentAnimator = child.animate()
            .translationX(targetX.toFloat())
            .setInterpolator(interpolator)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }
            })
    }

    /**
     * Edge case when expandable RecyclerView is collapsed and can't be scrolled
     */
    private fun assureBottomViewVisibility(target: View, child: V) {
        val recycler = target as? RecyclerView
        recycler?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (!recycler.canScrollVertically(DOWN) && !recycler.canScrollVertically(UP)) {
                    slideUp(child)
                }
            }
        })
    }
}

private const val ENTER_ANIMATION_DURATION = 225L
private const val EXIT_ANIMATION_DURATION = 175L
private const val STATE_SCROLLED_DOWN = 1
private const val STATE_SCROLLED_UP = 2
private const val DOWN = 1
private const val UP = -1
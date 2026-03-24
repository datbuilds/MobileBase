package vn.shb.cam.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.disableOverScrollMode() {
    this.recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
}

val ViewPager2.recyclerView: RecyclerView
    get() {
        return this[0] as RecyclerView
    }

fun ViewPager2.setCurrentItem(
    item: Int, duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator) { /* Ignored */
        }

        override fun onAnimationRepeat(animation: Animator) { /* Ignored */
        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}


fun ViewPager2.fadeAndScaleToItem(
    targetItem: Int,
    duration: Long = 200L,
    scaleFactor: Float = 1f // scale nhẹ nhàng
) {
    val recyclerView = getChildAt(0) as? RecyclerView ?: return

    val currentView = recyclerView.findViewHolderForAdapterPosition(currentItem)?.itemView
    val nextView = recyclerView.findViewHolderForAdapterPosition(targetItem)?.itemView

    if (currentItem == targetItem || currentView == null || nextView == null) {
        setCurrentItem(targetItem, false)
        return
    }

    nextView.alpha = 0f
    nextView.scaleX = scaleFactor
    nextView.scaleY = scaleFactor
    nextView.visibility = View.VISIBLE

    val animatorSet = AnimatorSet().apply {
        playTogether(
            ObjectAnimator.ofFloat(currentView, View.ALPHA, 1f, 0f),
            ObjectAnimator.ofFloat(currentView, View.SCALE_X, 1f, scaleFactor),
            ObjectAnimator.ofFloat(currentView, View.SCALE_Y, 1f, scaleFactor),

            ObjectAnimator.ofFloat(nextView, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(nextView, View.SCALE_X, scaleFactor, 1f),
            ObjectAnimator.ofFloat(nextView, View.SCALE_Y, scaleFactor, 1f)
        )
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                setCurrentItem(targetItem, false)
                currentView.alpha = 1f
                currentView.scaleX = 1f
                currentView.scaleY = 1f
            }
        })
    }

    animatorSet.start()
}


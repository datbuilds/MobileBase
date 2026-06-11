package com.mobile.base.utils.view.scrollable

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior

class BottomSheetBehaviour(context: Context?, attrs: AttributeSet?) :
    ScrollingViewBehavior(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        setDimensions(
            child as CoordinatorLayout, child.getLayoutParams().width,
            parent.height - getOffset(parent).toInt()
        )
        return true
    }

    override fun layoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        val dependencies = parent.getDependencies(child)
        val header: View? = findFirstDependency(dependencies)
        val available = Rect()
        if (header != null) {
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            available[parent.paddingLeft + lp.leftMargin, header.bottom + lp.topMargin, parent.width - parent.paddingRight - lp.rightMargin] =
                parent.height + header.bottom - parent.paddingBottom - lp.bottomMargin
            val out = Rect()
            GravityCompat.apply(
                resolveGravity(lp.gravity), child.measuredWidth,
                child.measuredHeight, available, out, layoutDirection
            )
            val overlap = overlapPixelsForOffset
            child.layout(out.left, out.top - overlap, out.right, parent.height - overlap)
        }
    }

    private val overlapPixelsForOffset: Int
        get() = if (overlayTop == 0) 0 else constrain(
            (1f * overlayTop).toInt(), 0,
            overlayTop
        )

    private fun findFirstDependency(views: List<View>): AppBarLayout? {
        var i = 0
        val z = views.size
        while (i < z) {
            val view = views[i]
            if (view is AppBarLayout) {
                return view
            }
            i++
        }
        return null
    }

    private fun setDimensions(view: CoordinatorLayout, width: Int, height: Int) {
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    private fun getOffset(coordinatorLayout: CoordinatorLayout): Float {
        for (i in 0 until coordinatorLayout.childCount) {
            val child = coordinatorLayout.getChildAt(i)
            if (child is AppBarLayout) {
                return child.getY() + child.getHeight()
            }
        }
        return 0F
    }

    companion object {
        private fun constrain(amount: Int, low: Int, high: Int): Int {
            return if (amount < low) low else if (amount > high) high else amount
        }

        private fun resolveGravity(gravity: Int): Int {
            return if (gravity == Gravity.NO_GRAVITY) GravityCompat.START or Gravity.TOP else gravity
        }
    }
}
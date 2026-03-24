package vn.shb.cam.utils.extensions

/**
 *   source: https://bladecoder.medium.com/fixing-recyclerview-nested-scrolling-in-opposite-direction-f587be5c1a04
 */

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import vn.shb.cam.utils.view.recyclerview.RecyclerViewMarginItemDecoration
import kotlin.math.abs


private const val TOOLBAR_MARGIN_SIZE_DP = 8

class RecyclerViewToolbarItemDecoration(toolbarSize: Int) :
    RecyclerViewMarginItemDecoration(toolbarSize + TOOLBAR_MARGIN_SIZE_DP, isTop = true)

private const val FAB_MARGIN_SIZE_DP = 8

class RecyclerViewFabItemDecoration(fabSize: Int) :
    RecyclerViewMarginItemDecoration(fabSize + FAB_MARGIN_SIZE_DP, isTop = false)

private const val BOTTOM_MARGIN_SIZE_DP = 8

class RecyclerViewBottomNavigationViewFabItemDecoration(bottomSize: Int, fabSize: Int) :
    RecyclerViewMarginItemDecoration(bottomSize + fabSize + FAB_MARGIN_SIZE_DP, isTop = false)

class RecyclerViewBottomNavigationViewItemDecoration(bottomSize: Int) :
    RecyclerViewMarginItemDecoration(bottomSize + BOTTOM_MARGIN_SIZE_DP, isTop = false)

fun RecyclerView.applyToolbarNavigationViewWithFabInsets(
    toolbarSize: Int = 0,
    bottomSize: Int = 0,
    fabSize: Int = 0
) {
    applyVerticalInsets()
    clipToPadding = false
    addItemDecoration(RecyclerViewToolbarItemDecoration(toolbarSize))
    addItemDecoration(RecyclerViewBottomNavigationViewFabItemDecoration(bottomSize, fabSize))
}


fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

private class SingleScrollDirectionEnforcer : RecyclerView.OnScrollListener(), OnItemTouchListener {

    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var scrollPointerId = -1
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var dx = 0
    private var dy = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrollPointerId = e.getPointerId(0)
                initialTouchX = (e.x + 0.5f).toInt()
                initialTouchY = (e.y + 0.5f).toInt()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = e.actionIndex
                scrollPointerId = e.getPointerId(actionIndex)
                initialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                initialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(scrollPointerId)
                if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    val x = (e.getX(index) + 0.5f).toInt()
                    val y = (e.getY(index) + 0.5f).toInt()
                    dx = x - initialTouchX
                    dy = y - initialTouchY
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val oldState = scrollState
        scrollState = newState
        if (oldState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            recyclerView.layoutManager?.let { layoutManager ->
                val canScrollHorizontally = layoutManager.canScrollHorizontally()
                val canScrollVertically = layoutManager.canScrollVertically()
                if (canScrollHorizontally != canScrollVertically) {
                    if ((canScrollHorizontally && abs(dy) > abs(dx))
                        || (canScrollVertically && abs(dx) > abs(dy))
                    ) {
                        recyclerView.stopScroll()
                    }
                }
            }
        }
    }
}

fun RecyclerView.setVisibleOrGone(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}
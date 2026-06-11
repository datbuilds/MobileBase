package com.mobile.base.utils.view.paing

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class EndlessRecyclerOnScrollListener(
    val HIDE_THRESHOLD: Int = 5,
    val onBottomListener: OnBottomListener
) : RecyclerView.OnScrollListener() {

    enum class LAYOUT_MANAGER_TYPE {
        LINEAR, GRID, STAGGERED_GRID
    }

    protected var layoutManagerType: LAYOUT_MANAGER_TYPE? = null
    private var firstVisibleItemPosition = 0
    private var lastVisibleItemPosition = 0
    private var currentScrollState = 0
    private var isScrollDown = false

    private var isLoading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        //  int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            layoutManagerType = if (layoutManager is LinearLayoutManager) {
                LAYOUT_MANAGER_TYPE.LINEAR
            } else if (layoutManager is GridLayoutManager) {
                LAYOUT_MANAGER_TYPE.GRID
            } else if (layoutManager is StaggeredGridLayoutManager) {
                LAYOUT_MANAGER_TYPE.STAGGERED_GRID
            } else {
                throw RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager"
                )
            }
        }
        when (layoutManagerType) {
            LAYOUT_MANAGER_TYPE.LINEAR -> {
                firstVisibleItemPosition =
                    (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                lastVisibleItemPosition = layoutManager!!.findLastVisibleItemPosition()
            }

            LAYOUT_MANAGER_TYPE.GRID -> {
                firstVisibleItemPosition =
                    (layoutManager as GridLayoutManager?)!!.findFirstVisibleItemPosition()
                lastVisibleItemPosition = layoutManager!!.findLastVisibleItemPosition()
            }

            LAYOUT_MANAGER_TYPE.STAGGERED_GRID -> {
                val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager?
                val firstPositions = IntArray(staggeredGridLayoutManager!!.spanCount)
                val lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions)
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions)
                firstVisibleItemPosition = findMin(firstPositions)
                lastVisibleItemPosition = findMax(lastPositions)
            }

            else -> {}
        }
//        onBottomListener.onScroll(firstVisibleItemPosition, lastVisibleItemPosition, dx, dy)
//        isScrollDown = dy > 0
//        if (lastVisibleItemPosition > HIDE_THRESHOLD) {
//            if (isScrollDown) {
//                onBottomListener.onHide()
//            } else {
//                onBottomListener.onShow()
//            }
//        } else onBottomListener.onHide()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        currentScrollState = newState
        val layoutManager = recyclerView.layoutManager
        val visibleItemCount = layoutManager!!.childCount
        val totalItemCount = layoutManager.itemCount
        if (visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition >= totalItemCount - HIDE_THRESHOLD) {
            isLoading = true
            onBottomListener.onBottom()
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    private fun findMin(lastPositions: IntArray): Int {
        var min = lastPositions[0]
        for (value in lastPositions) {
            if (value < min) {
                min = value
            }
        }
        return min
    }

    fun getLoaded() {
        isLoading = false
    }

    companion object {
        private const val TAG = "EndlessRecyclerOnScrollListener"
    }
}
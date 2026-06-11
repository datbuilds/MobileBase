package com.mobile.base.utils.view.recyclerview

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OnRecyclerSnapScroll(
    private val recyclerView: RecyclerView,
    private val listener: VisibilityListener
) : RecyclerView.OnScrollListener() {

    private val layoutManager = recyclerView.layoutManager as LinearLayoutManager

    private val rvGlobalRect = Rect()
    private val rvLocalRect = Rect()
    private val itemBoundsRect = Rect()

    private var positionShowing: Int = 0
    private var positionHiding: Int = 0
    private var visibilty: Float = 0f

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val isScrollRight = dx >= 0
        getItems(isScrollRight)
    }

    private fun getItems(isScrollRight: Boolean) {
        recyclerView.getGlobalVisibleRect(rvGlobalRect)
        recyclerView.getLocalVisibleRect(rvLocalRect)

        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        calculateVisibility(isScrollRight, firstVisibleItem, lastVisibleItem)
    }

    private fun calculateVisibility(
        isScrollRight: Boolean,
        firstVisibleItem: Int,
        lastVisibleItem: Int
    ) {
        layoutManager.findViewByPosition(firstVisibleItem)?.let { item ->
            item.getGlobalVisibleRect(itemBoundsRect)
            val itemSize = layoutManager.findViewByPosition(firstVisibleItem)?.width
            val visibleSize: Int


            var isHiding: Boolean
            if (itemBoundsRect.right >= rvLocalRect.right) { //right
                isHiding = !isScrollRight
                visibleSize = rvGlobalRect.right - itemBoundsRect.left
            } else { //left
                isHiding = isScrollRight
                visibleSize = itemBoundsRect.right - rvGlobalRect.left
            }

            visibilty = (visibleSize * 100 / itemSize!!).toFloat()
            if (visibilty < 0) {
                visibilty *= -1
            }

            if (visibilty >= 100) {
                visibilty = 100f
                isHiding = false
            }

            if (isHiding) {
                positionHiding = firstVisibleItem
                visibilty = 100 - visibilty
                positionShowing = lastVisibleItem
            } else {
                positionShowing = firstVisibleItem
                positionHiding = lastVisibleItem
            }

            if (positionShowing != positionHiding) {
                listener.onVisibleChanged(positionShowing, positionHiding, visibilty)
            }
        }
    }
}
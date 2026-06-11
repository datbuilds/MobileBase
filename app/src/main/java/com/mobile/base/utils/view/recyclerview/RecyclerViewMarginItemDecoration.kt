package com.mobile.base.utils.view.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mobile.base.utils.extensions.dp2px

open class RecyclerViewMarginItemDecoration(
    private val sizeInDp: Int = 16,
    private val isTop: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isTop) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = sizeInDp.dp2px
            }
        } else {
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                outRect.bottom = sizeInDp.dp2px
            }
        }
    }
}
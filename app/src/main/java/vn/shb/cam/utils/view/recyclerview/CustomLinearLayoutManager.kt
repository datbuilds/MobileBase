package vn.shb.cam.utils.view.recyclerview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    private var isScrollEnabled = true

    fun setScrollEnabled(scrollEnabled: Boolean) {
        isScrollEnabled = scrollEnabled
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}

package vn.shb.cam.utils.extensions

import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Extension function để force RecyclerView recalculate layout sau khi submitList
 * Giải quyết vấn đề RecyclerView không thay đổi height khi data thay đổi
 */
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.forceRecalculateLayout(
    adapter: ListAdapter<T, VH>,
    newList: List<T>?,
    onComplete: (() -> Unit)? = null
) {
    adapter.submitList(newList) {
        // Callback khi submitList hoàn thành
        this.post {
            // Force layout recalculation
            this.requestLayout()

            // Nếu có parent view, cũng force parent recalculate
            (parent as? View)?.requestLayout()

            onComplete?.invoke()
        }
    }
}

/**
 * Extension function để force RecyclerView và parent views recalculate layout
 */
fun RecyclerView.forceLayoutRecalculation() {
    this.post {
        this.requestLayout()
        (parent as? View)?.requestLayout()
    }
}

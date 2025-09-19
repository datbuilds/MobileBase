package vn.shb.lao.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import vn.shb.lao.databinding.ItemLoadingBinding

abstract class BaseLoadMoreAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val diffCallback: DiffUtil.ItemCallback<T>,
    private val onLoadMore: ((page: Int) -> Unit)? = null,
    private val recyclerView: RecyclerView? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        if (recyclerView != null && recyclerView.layoutManager is LinearLayoutManager && onLoadMore != null) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val totalItemCount = linearLayoutManager.itemCount
                        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                        if (!isLoading && totalItemCount <= (lastVisibleItem + LOAD_MORE_VISIBLE_THRESHOLD)) {
                            items.add(null)
                            notifyItemInserted(items.size - 1)
                            isLoading = true
                            currentPage++
                            onLoadMore.invoke(currentPage)
                        }
                    }
                }
            })
        }
    }

    val items = mutableListOf<T?>()
    private var isLoading = false
    private var currentPage = 0

    open fun currentPage() = currentPage

    abstract fun createBinding(parent: ViewGroup): VB

    abstract fun bind(binding: VB, item: T)

    abstract fun isLastItem(item: T?): Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = createBinding(parent)
                ItemViewHolder(binding)
            }

            VIEW_TYPE_LOADING -> {
                val loadingBinding = ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(loadingBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                val item = items[position]
                if (item != null) {
                    bind((holder as BaseLoadMoreAdapter<*, *>.ItemViewHolder).binding as VB, item)
                }
            }

            VIEW_TYPE_LOADING -> {

            }
        }
    }

    open fun removeItemLoading() {
        if (items.last() == null) {
            items.removeLast()
            isLoading = true
            update()
        }
    }

    open fun addLoadMore(data: List<T>) {
        items.removeLast()
        addAll(data)
        isLoading = false
        update()
    }

    open fun addAll(list: List<T>) {
        items.addAll(list)
        update()
    }

    open fun add(element: T?) {
        items.add(element)
        update()
    }

    open fun update() {
        notifyDataSetChanged()
    }

    open fun set(list: List<T>) {
        clear()
        addAll(list)
    }

    open fun items(): MutableList<T?> {
        return items
    }

    open fun clear() {
        items.clear()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun submitList(newItems: List<T>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = newItems.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition]?.let {
                    diffCallback.areItemsTheSame(it, newItems[newItemPosition])
                } == true

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition]?.let {
                    diffCallback.areContentsTheSame(it, newItems[newItemPosition])
                } == true
        })

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
        isLoading = false
    }

    fun resetCurrentPage() {
        currentPage = 0
    }

    inner class ItemViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
        private const val LOAD_MORE_VISIBLE_THRESHOLD = 5
    }
}

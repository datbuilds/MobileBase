package vn.shb.sale.base

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import vn.shb.lao.databinding.ItemLoadingBinding

abstract class BaseLoadMoreAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val recyclerView: RecyclerView? = null,
    private val onLoadMore: ((page: Int) -> Unit)? = null,
    private val startPage: Int = 0
) : ListAdapter<BaseLoadMoreAdapter.ListItem<T>, RecyclerView.ViewHolder>(Diff(diffCallback)) {

    private var currentPage = startPage
    private var isLoading = false
    private var scrollListener: RecyclerView.OnScrollListener? = null

    sealed class ListItem<T> {
        data class Data<T>(val data: T) : ListItem<T>()
        class Loading<T> : ListItem<T>()
    }

    val items: List<T>
        get() = currentList.filterIsInstance<ListItem.Data<T>>().map { it.data }

    init {
        setupLoadMoreListener()
    }

    // ---------- Load More ----------
    private fun setupLoadMoreListener() {
        val rv = recyclerView ?: return
        val lm = rv.layoutManager as? LinearLayoutManager ?: return
        val callback = onLoadMore ?: return

        scrollListener?.let { rv.removeOnScrollListener(it) }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0 || isLoading) return
                val total = itemCount
                val last = lm.findLastVisibleItemPosition()
                if (last + THRESHOLD >= total) triggerLoadMore()
            }
        }

        rv.addOnScrollListener(scrollListener!!)
    }

    private fun triggerLoadMore() {
        if (isLoading) return
        isLoading = true
        addLoadingFooter()
        currentPage++
        onLoadMore?.invoke(currentPage)
    }

    private fun addLoadingFooter() {
        submitList(currentList + listOf(ListItem.Loading()))
    }

    fun removeLoadingItem() {
        val newList = currentList.filterNot { it is ListItem.Loading }
        submitList(newList)
        isLoading = false
    }

    fun resetPage() {
        currentPage = startPage
    }

    fun rollbackPage() {
        if (currentPage > startPage) currentPage--
    }

    fun getCurrentPage() = currentPage

    // ---------- Update / Add ----------
    fun updateItem(position: Int, newItem: T) {
        if (position !in items.indices) return
        val currentData = items.toMutableList()
        currentData[position] = newItem
        submitList(currentData.map { ListItem.Data(it) })
        highlight(position)
    }

    fun addLoadMore(newItems: List<T>) {
        val currentData = currentList.filterIsInstance<ListItem.Data<T>>().toMutableList()
        currentData.addAll(newItems.map { ListItem.Data(it) })

        // Submit list mới, không có Loading
        submitList(currentData.toList())

        isLoading = false
    }

    // ---------- Adapter overrides ----------
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Data -> TYPE_ITEM
            is ListItem.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_ITEM -> ItemHolder(createBinding(parent))
            TYPE_LOADING -> LoadingHolder(
                ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> error("Unknown viewType $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is BaseLoadMoreAdapter<*, *>.ItemHolder && item is ListItem.Data) {
            bind(holder.binding as VB, item.data)
        }
    }

    // ---------- Highlight ----------
    private fun highlight(position: Int) {
        val rv = recyclerView ?: return

        rv.post {
            val lm = rv.layoutManager as? LinearLayoutManager ?: return@post
            val first = lm.findFirstVisibleItemPosition()
            val last = lm.findLastVisibleItemPosition()

            if (position !in first..last) {
                rv.smoothScrollToPosition(position)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val holder = rv.findViewHolderForAdapterPosition(position) ?: return@postDelayed
                val view = holder.itemView

                val context = view.context
                val original = ContextCompat.getColor(context, vn.shb.core.R.color.white)
                val highlight = 0xFFF7FAFC.toInt()

                ValueAnimator.ofObject(ArgbEvaluator(), original, highlight, original).apply {
                    duration = 800
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { anim ->
                        view.setBackgroundColor(anim.animatedValue as Int)
                    }
                }.start()
            }, 120)
        }
    }

    // ---------- Abstract ----------
    abstract fun createBinding(parent: ViewGroup): VB
    abstract fun bind(binding: VB, item: T)

    inner class ItemHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
    inner class LoadingHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_LOADING = 0
        private const val THRESHOLD = 5

        private class Diff<T : Any>(private val itemDiffCallback: DiffUtil.ItemCallback<T>) :
            DiffUtil.ItemCallback<ListItem<T>>() {
            override fun areItemsTheSame(oldItem: ListItem<T>, newItem: ListItem<T>): Boolean {
                return when {
                    oldItem is ListItem.Data && newItem is ListItem.Data ->
                        itemDiffCallback.areItemsTheSame(oldItem.data, newItem.data)

                    oldItem is ListItem.Loading && newItem is ListItem.Loading -> true
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: ListItem<T>, newItem: ListItem<T>): Boolean {
                return when {
                    oldItem is ListItem.Data && newItem is ListItem.Data ->
                        itemDiffCallback.areContentsTheSame(oldItem.data, newItem.data)

                    oldItem is ListItem.Loading && newItem is ListItem.Loading -> true
                    else -> false
                }
            }
        }
    }
}

package vn.shb.cam.screens.paste2pay.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.BsClipboardChooserBinding
import vn.shb.cam.databinding.ItemClipboardOptionBinding

class BSClipboardChooser(
    private val items: List<ClipboardOption>,
    private val onItemSelected: ((ClipboardOption) -> Unit)?
) :
    BaseBottomDialogBinding<BsClipboardChooserBinding>(BsClipboardChooserBinding::inflate) {

    data class ClipboardOption(
        val fullText: String,
        val charCount: Int,
        val lineCount: Int
    )

    companion object {
        const val TAG = "BSClipboardChooser"
    }

    class Builder {
        private val items = mutableListOf<ClipboardOption>()
        private var onItemSelected: ((ClipboardOption) -> Unit)? = null

        fun setItems(items: List<ClipboardOption>) = apply {
            this.items.clear()
            this.items.addAll(items)
        }

        fun setOnItemSelected(onItemSelected: (ClipboardOption) -> Unit) = apply {
            this.onItemSelected = onItemSelected
        }

        fun build() = BSClipboardChooser(items.toList(), onItemSelected)
    }

    private val clipboardAdapter = ClipboardOptionAdapter { item ->
        onItemSelected?.invoke(item)
        dismissAllowingStateLoss()
    }

    override fun handleSavedState(savedInstanceState: Bundle?) {
        super.handleSavedState(savedInstanceState)
        isCancelable = true
    }

    override fun initView(view: View) {
        binding.rvClipboardOptions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clipboardAdapter
            setHasFixedSize(true)
        }
        clipboardAdapter.submitList(items)
    }

    override fun initListener() {
        binding.ivClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun initObserve() {
    }

    private class ClipboardOptionAdapter(
        private val onItemClick: (ClipboardOption) -> Unit
    ) : ListAdapter<ClipboardOption, ClipboardOptionAdapter.ClipboardOptionViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipboardOptionViewHolder {
            val binding = ItemClipboardOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ClipboardOptionViewHolder(binding, onItemClick)
        }

        override fun onBindViewHolder(holder: ClipboardOptionViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ClipboardOptionViewHolder(
            private val binding: ItemClipboardOptionBinding,
            private val onItemClick: (ClipboardOption) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: ClipboardOption) {
                with(binding) {
                    tvMeta.text = binding.root.context.getString(
                        vn.shb.cam.R.string.clipboard_sheet_meta,
                        item.charCount,
                        item.lineCount
                    )
                    tvPreview.text = item.fullText
                    cardClipboardOption.setOnClickListener {
                        onItemClick(item)
                    }
                }
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<ClipboardOption>() {
                override fun areItemsTheSame(oldItem: ClipboardOption, newItem: ClipboardOption): Boolean {
                    return oldItem.fullText == newItem.fullText
                }

                override fun areContentsTheSame(oldItem: ClipboardOption, newItem: ClipboardOption): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}

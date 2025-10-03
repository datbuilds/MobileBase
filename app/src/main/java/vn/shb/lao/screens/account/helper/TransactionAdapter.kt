package vn.shb.lao.screens.account.helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.shb.lao.R
import vn.shb.lao.databinding.ItemHeaderBinding
import vn.shb.lao.databinding.ItemTransactionBinding
import vn.shb.lao.screens.account.model.TransactionItem

class TransactionAdapter(private val items: List<TransactionItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickDetail: (TransactionItem) -> Unit = {}

    companion object {
        private val TYPE_HEADER = 0
        private val TYPE_TRANSACTION = 1
    }

    public fun setOnClickDetailListener(listener: (TransactionItem) -> Unit) {
        onClickDetail = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactionItem.Header -> TYPE_HEADER
            is TransactionItem.Transaction -> TYPE_TRANSACTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding =
                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding =
                ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TransactionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is HeaderViewHolder && item is TransactionItem.Header) {
            holder.bind(item)
        } else if (holder is TransactionViewHolder && item is TransactionItem.Transaction) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // --- ViewHolder cho Header ---
    class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionItem.Header) {
            binding.root.text = item.title
        }
    }

    // --- ViewHolder cho Transaction ---
    class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionItem.Transaction) {
            binding.tvName.text = item.name
            binding.tvSubInfo.text = item.subInfo

            val amountText = (if (item.isIncome) "+" else "-") +
                    "%,d".format(item.amount) + " " + item.currency
            binding.tvAmount.text = amountText

            binding.tvAmount.setTextColor(
                if (item.isIncome) Color.parseColor("#00AA00")
                else Color.RED
            )

            binding.icon.setImageResource(
                if (item.isIncome) R.drawable.ic_in_come
                else R.drawable.ic_out_come
            )
        }
    }
}


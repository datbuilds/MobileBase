package vn.shb.lao.screens.account.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.home.TransactionItem
import vn.shb.lao.R
import vn.shb.lao.databinding.ItemHeaderBinding
import vn.shb.lao.databinding.ItemTransactionBinding
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.common.Const.SEPARATOR_SPACE

class TransactionAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: ArrayList<TransactionItem> = arrayListOf()
    private var onClickDetail: (TransactionItem.Transaction) -> Unit = {}

    companion object {
        private val TYPE_HEADER = 0
        private val TYPE_TRANSACTION = 1
    }

    fun submitList(newItems: List<TransactionItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setOnClickDetailListener(listener: (TransactionItem.Transaction) -> Unit) {
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
    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionItem.Header) {
            binding.root.text = item.title
        }
    }

    // --- ViewHolder cho Transaction ---
    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionItem.Transaction) {

            with(binding) {
                tvName.text = item.transactionDescription
//            tvSubInfo.text = item.subInfo

                val isIncome = !item.amountFormatted.startsWith("-")

                val amountValue =
                    if (isIncome) Const.CONG.plus(item.creditAmount.getBalance()) else Const.TRU.plus(
                        item.debitAmount.getBalance()
                    )

                val amountText = amountValue + SEPARATOR_SPACE + item.currencyCode
                tvAmount.text = amountText

                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (isIncome) R.color.color_income
                        else R.color.color_outcome
                    )
                )
                icon.setImageResource(
                    if (isIncome) R.drawable.ic_in_come
                    else R.drawable.ic_out_come
                )

                root.setOnSingleClickListener {
                    onClickDetail.invoke(item)
                }
            }
        }
    }
}


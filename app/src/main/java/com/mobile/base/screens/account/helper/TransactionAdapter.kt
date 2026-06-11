package com.mobile.base.screens.account.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mobile.base.R
import com.mobile.base.databinding.ItemTransactionBinding
import com.mobile.base.screens.account.helper.TransactionAdapter.TransactionViewHolder
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.common.Const.SEPARATOR_SPACE
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.getBalance
import com.mobile.base.data.entities.home.TransactionItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionAdapter() : RecyclerView.Adapter<TransactionViewHolder>() {
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

//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is TransactionItem.Header -> TYPE_HEADER
//            is TransactionItem.Transaction -> TYPE_TRANSACTION
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {

        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)

//        return if (viewType == TYPE_HEADER) {
//            val binding =
//                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            HeaderViewHolder(binding)
//        } else {
//            val binding =
//                ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            TransactionViewHolder(binding)
//        }
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        val checkHeader=try {
            items[position+1] is TransactionItem.Header
        }catch (e: Exception){
            false
        }

        holder.bind(item,checkHeader)
//        if (holder is HeaderViewHolder && item is TransactionItem.Header) {
//            holder.bind(item)
//        } else if (holder is TransactionViewHolder && item is TransactionItem.Transaction) {
//            holder.bind(item)
//        }
    }

    override fun getItemCount(): Int = items.size

    // --- ViewHolder cho Header ---
//    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: TransactionItem.Header) {
//            binding.root.text = getDisplayDate(binding.root.context,item.title)
//        }
//
//        fun getDisplayDate(context: Context,dateString: String): String {
//            return try {
//                // 1. Định nghĩa format cho chuỗi đầu vào
//                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//
//                // 2. Parse chuỗi string sang đối tượng LocalDate
//                val inputDate = LocalDate.parse(dateString, formatter)
//
//                // 3. Lấy ngày hiện tại (Hệ thống)
//                val today = LocalDate.now()
//                val yesterday = today.minusDays(1)
//
//                // 4. So sánh
//                when (inputDate) {
//                    today -> context.getString(R.string.today)
//                    yesterday ->  context.getString(R.string.yesterday)
//                    else -> dateString // Giữ nguyên format cũ nếu xa hơn
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                dateString // Trả về text gốc nếu parse lỗi
//            }
//        }
//    }

    // --- ViewHolder cho Transaction ---
    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getDisplayDate(context: Context,dateString: String): String {
            return try {
                // 1. Định nghĩa format cho chuỗi đầu vào
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                // 2. Parse chuỗi string sang đối tượng LocalDate
                val inputDate = LocalDate.parse(dateString, formatter)

                // 3. Lấy ngày hiện tại (Hệ thống)
                val today = LocalDate.now()
                val yesterday = today.minusDays(1)

                // 4. So sánh
                when (inputDate) {
                    today -> context.getString(R.string.today)
                    yesterday ->  context.getString(R.string.yesterday)
                    else -> dateString // Giữ nguyên format cũ nếu xa hơn
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dateString // Trả về text gốc nếu parse lỗi
            }
        }

        fun bind(item: TransactionItem,checkHeader: Boolean) {

            with(binding) {
                if (item is TransactionItem.Header){
                    layoutTitle.text=  getDisplayDate(binding.root.context,item.title)
                    layoutTitle.isVisible=true
                    layoutContent.isVisible=false
                    vLineGray.isVisible=false
                    return
                }


                if (item is TransactionItem.Transaction){
                    layoutTitle.isVisible=false
                    layoutContent.isVisible=true

                    vLineGray.isVisible = !checkHeader


                    tvName.text = item.transactionDescription

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
}


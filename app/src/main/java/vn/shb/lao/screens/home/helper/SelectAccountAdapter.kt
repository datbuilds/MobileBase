package vn.shb.lao.screens.home.helper

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.R
import vn.shb.lao.databinding.ItemAccountSelectBinding
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.visible

class SelectAccountAdapter(
    private val items: List<AccountInfo>,
    private val onClickAccount: (AccountInfo) -> Unit
) : RecyclerView.Adapter<SelectAccountAdapter.AccountInfoViewHolder>() {

    inner class AccountInfoViewHolder(val binding: ItemAccountSelectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountInfoViewHolder {
        val binding = ItemAccountSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountInfoViewHolder, position: Int) {
        val accountItem = items[position]
        holder.binding.apply {
            tvInfoAccount.text = root.context.getString(
                R.string.account_info_format,
                accountItem.accountNumber,
                accountItem.accountType,
                accountItem.currencyCode
            )
            tvValueBalance.bindBalance(
                String.format("%,.0f", accountItem.availableBalance),
                " ${accountItem.currencyCode}"
            )
            if (accountItem.isSelected) {
                root.setBackgroundResource(R.drawable.bg_selected_account)
                ivSelectAccount.visible()
            } else {
                root.setBackgroundColor(Color.TRANSPARENT)
                ivSelectAccount.gone()
            }
            root.setOnSingleClickListener {
                items.forEach {
                    it.isSelected = it.accountNumber == accountItem.accountNumber
                }
                notifyDataSetChanged()
                onClickAccount.invoke(accountItem)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

fun TextView.bindBalance(amount: String, currency: String) {
    val textView = this

    val fullText = amount + currency

    val spannable = SpannableString(fullText)

    spannable.setSpan(
        ForegroundColorSpan("#BFBFBF".toColorInt()), // màu xám nhạt
        amount.length,
        fullText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        amount.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView.text = spannable
}

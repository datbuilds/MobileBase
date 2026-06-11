package com.mobile.base.screens.home.helper

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
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.R
import com.mobile.base.databinding.ItemAccountSelectBinding
import com.mobile.base.screens.home.getTypeAccount
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.visible

class SelectAccountAdapter(
    private val items: List<AccountBase>,
    val isCanSelect : Boolean = true,
    private val onClickAccount: (AccountBase) -> Unit
) : RecyclerView.Adapter<SelectAccountAdapter.AccountBaseViewHolder>() {

    inner class AccountBaseViewHolder(val binding: ItemAccountSelectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountBaseViewHolder {
        val binding = ItemAccountSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountBaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountBaseViewHolder, position: Int) {
        val accountItem = items[position]
        holder.binding.apply {
            val valueAccount = getTypeAccount(context = root.context, accountItem)
            tvInfoAccount.text = root.context.getString(
                R.string.account_info_format,
                accountItem.accountNumber,
                valueAccount,
                accountItem.currencyCode
            )
            tvValueBalance.bindBalance(
                accountItem.getAvailableBalance(),
                " ${accountItem.currencyCode}"
            )
            if (accountItem.isSelected() && isCanSelect) {
                root.setBackgroundResource(R.drawable.bg_selected_account)
                ivSelectAccount.visible()
                viewLineAccount.gone()
            } else {
                root.setBackgroundColor(Color.TRANSPARENT)
                ivSelectAccount.gone()
                viewLineAccount.visible()
            }
            root.setOnSingleClickListener {
                if (isCanSelect){
                    items.forEach {
                        it.setSelected(it.accountNumber == accountItem.accountNumber)
                    }
                    notifyDataSetChanged()
                    onClickAccount.invoke(accountItem)
                }
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

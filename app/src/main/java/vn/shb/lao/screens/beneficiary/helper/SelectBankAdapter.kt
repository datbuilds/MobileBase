package vn.shb.lao.screens.beneficiary.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.lao.databinding.ItemBankSelectBinding
import vn.shb.lao.utils.BankType

class SelectBankAdapter(
    private var items: List<Bank>,
    private var selectedCode: String? = null,
    private val onClickBank: (Bank) -> Unit
) : RecyclerView.Adapter<SelectBankAdapter.BankViewHolder>() {

    inner class BankViewHolder(val binding: ItemBankSelectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding = ItemBankSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val bank = items[position]
        holder.binding.apply {
            tvBankCode.text = bank.bankCode
            tvBankName.text = bank.bankName ?: bank.shortName
            ivLogo.setImageResource(BankType.getIconByCode(bank.bankCode))

            val isSelected = bank.bankCode == selectedCode
            if (isSelected) {
                // Background usually defined in xml or via resource. 
                // item_bank_select.xml tools:background="@drawable/bg_selected_account"
                // Let's assume we need to set it manually or it's handled by state list?
                // The xml doesn't have a selector background, it's just a constraint layout.
                // We need to set it here.
                root.setBackgroundResource(vn.shb.lao.R.drawable.bg_selected_account)
                ivCheck.visibility = View.VISIBLE
                viewDivider.visibility = View.INVISIBLE // Hide divider if selected? Or keep it? keeping it is safer usually unless designed otherwise.
                // Reference image shows orange background.
            } else {
                root.setBackgroundResource(android.R.color.transparent)
                ivCheck.visibility = View.GONE
                viewDivider.visibility = View.VISIBLE
            }

            root.setOnSingleClickListener {
                selectedCode = bank.bankCode
                notifyDataSetChanged()
                onClickBank.invoke(bank)
            }
        }
    }

    override fun getItemCount(): Int = items.size
    
    fun updateData(newItems: List<Bank>) {
        items = newItems
        notifyDataSetChanged()
    }
}

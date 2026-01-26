package vn.shb.cam.screens.home.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.cam.databinding.ItemBeneficiarySelectBinding
import vn.shb.cam.utils.BankType

class SelectBeneficiaryAdapter(
    private var items: List<Beneficiary>,
    private val onClick: (Beneficiary) -> Unit
) : RecyclerView.Adapter<SelectBeneficiaryAdapter.BeneficiaryViewHolder>() {

    inner class BeneficiaryViewHolder(val binding: ItemBeneficiarySelectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder {
        val binding = ItemBeneficiarySelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BeneficiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvAccountName.text = item.accountName
            tvAccountNumber.text = item.accountNumber
            tvBankName.text = item.bankName
            ivBankLogo.setImageResource(BankType.getIconByCode(item.bankCode))

            root.setOnSingleClickListener {
                onClick.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Beneficiary>) {
        items = newItems
        notifyDataSetChanged()
    }
}

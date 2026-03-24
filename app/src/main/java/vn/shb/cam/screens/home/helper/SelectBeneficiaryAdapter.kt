package vn.shb.cam.screens.home.helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.Config
import androidx.recyclerview.widget.RecyclerView
import vn.shb.cam.R
import vn.shb.cam.databinding.ItemBeneficiarySelectBinding
import vn.shb.cam.utils.BankType
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.setTextOrGone
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary

class SelectBeneficiaryAdapter(
    private var items: List<Beneficiary>,
    private var beneficiarySelected: Beneficiary? = null,
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
            tvNickName.setTextOrGone(item.accountNick)
            tvBankName.text = item.bankName
            ivBankLogo.setImageResource(BankType.getIconByCode(item.bankCode))

            val isSelected = beneficiarySelected?.accountNumber == item.accountNumber

            if (isSelected){
                root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.bg_selected))
                ivV.visible()
            } else {
                root.setBackgroundColor(Color.TRANSPARENT)
                ivV.gone()
            }

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

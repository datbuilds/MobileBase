package vn.shb.lao.screens.beneficiary.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.lao.databinding.ItemBeneficiaryBinding

class BeneficiaryAdapter :
    ListAdapter<Beneficiary, BeneficiaryAdapter.BeneficiaryVH>(BankAccountDiffCallback()) {

    private var actionEditBeneficiary: ActionEditBeneficiary? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BeneficiaryVH {
        return BeneficiaryVH(
            ItemBeneficiaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BeneficiaryVH,
        position: Int
    ) {
        holder.bindItem(getItem(position))
    }


    inner class BeneficiaryVH(private val binding: ItemBeneficiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: Beneficiary) {
            with(binding) {
                tvNameUser.text = item.accountName ?: ""
                tvNumber.text = item.accountNumber ?: ""
                tvBank.text = item.bankName ?: ""

                ivEdit.setOnSingleClickListener {
                    actionEditBeneficiary?.edit(item)
                }

                ivDelete.setOnSingleClickListener {
                    actionEditBeneficiary?.delete(item)
                }
            }
        }
    }

    fun setListenAction(action: ActionEditBeneficiary) {
        actionEditBeneficiary = action
    }

    private var originalList = listOf<Beneficiary>()

    override fun submitList(list: List<Beneficiary>?) {
        super.submitList(list)
        if (originalList.isEmpty()) {
            originalList = list ?: emptyList()
        }
    }

    fun filter(keyword: String) {
        if (keyword.isBlank()) {
            submitList(originalList)
            return
        }

        val query = keyword.lowercase().trim()
        val filtered = originalList.filter {
            (it.accountName ?: "").lowercase().contains(query)
                    || (it.accountNumber ?: "").contains(query)
                    || (it.bankName ?: "").lowercase().contains(query)
        }

        submitList(filtered)
    }

    companion object {
        class BankAccountDiffCallback : DiffUtil.ItemCallback<Beneficiary>() {
            override fun areItemsTheSame(
                oldItem: Beneficiary,
                newItem: Beneficiary
            ): Boolean {
                return oldItem.accountNumber == newItem.accountNumber
            }

            override fun areContentsTheSame(
                oldItem: Beneficiary,
                newItem: Beneficiary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package vn.shb.cam.screens.login.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.shb.cam.R
import vn.shb.cam.databinding.ItemBranchBinding
import vn.shb.cam.screens.login.model.Branch
import vn.shb.cam.utils.extensions.setCustomSpannable

class BranchAdapter(
    private val items: List<Branch>,
    private val onAddressClick: (Int, Branch) -> Unit
) : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    companion object {
        const val CLICK_ADDRESS = 1
        const val CLICK_HOTLINE = 2
    }

    inner class BranchViewHolder(val binding: ItemBranchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val binding = ItemBranchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BranchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val branch = items[position]
        holder.binding.apply {
            tvBranchName.text = branch.name
            tvAddressDetail.setCustomSpannable(
                branch.address, R.color.color_hotline,
                R.color.color_hotlineClick
            ) { onAddressClick.invoke(CLICK_ADDRESS, branch) }
            tvHotlineDetail.setCustomSpannable(
                branch.tel, R.color.color_hotline,
                R.color.color_hotlineClick
            ) { onAddressClick.invoke(CLICK_HOTLINE, branch) }

        }
    }

    override fun getItemCount(): Int = items.size
}

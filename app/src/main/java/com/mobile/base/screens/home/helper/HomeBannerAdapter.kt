package com.mobile.base.screens.home.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.base.R
import com.mobile.base.databinding.ItemBannerBinding
import com.mobile.base.databinding.ItemBannerHomeBinding

class HomeBannerAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<HomeBannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(
        val binding: ItemBannerBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val marginStart = if (position == 0) {
            0
        } else {
            holder.itemView.context.resources.getDimension(R.dimen.margin_8dp).toInt()
        }
        (holder.itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            this.marginStart = marginStart
            this.marginEnd = 0
            holder.itemView.layoutParams = this
        }
        holder.binding.ivBanner.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}

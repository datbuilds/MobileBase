package com.mobile.base.screens.home.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.base.databinding.ItemBannerHomeBinding

class LoopingAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<LoopingAdapter.ImageViewHolder>() {

    // nhân 3 để có khối trước / giữa / sau
    private val loopedList = ArrayList<Int>().apply {
        addAll(images)
        addAll(images)
        addAll(images)
    }

    inner class ImageViewHolder(val binding: ItemBannerHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemBannerHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val resId = loopedList[position]
        holder.binding.imageView.setImageResource(resId)
    }

    override fun getItemCount(): Int = loopedList.size

    // số ảnh gốc
    fun getRealCount(): Int = images.size

    // vị trí bắt đầu (bắt đầu ở khối giữa)
    fun getMiddlePosition(): Int = images.size
}


/**
 * Copyright © 2021 Md. Mahmudul Hasan Shohag. All rights reserved.
 */

package com.example.imagecrouse.ui.whynotimagecarousel.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.imagecrouse.databinding.ItemCarouselBinding
import com.example.imagecrouse.ui.whynotimagecarousel.listener.CarouselListener
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselGravity
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselItem
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselType
import com.example.imagecrouse.ui.whynotimagecarousel.utils.CarouselItemDecoration
import com.example.imagecrouse.ui.whynotimagecarousel.utils.setImage

open class FiniteCarouselAdapter(
    private val recyclerView: RecyclerView,
    private val carouselType: CarouselType,
    private val carouselGravity: CarouselGravity,
    private val autoWidthFixing: Boolean,
    private val imageScaleType: ImageView.ScaleType,
    private val imagePlaceholder: Drawable?,
) : RecyclerView.Adapter<FiniteCarouselAdapter.MyViewHolder>() {
    var listener: CarouselListener? = null

    class MyViewHolder(
        val binding: ViewBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    protected val dataList: MutableList<CarouselItem> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val binding =
            listener?.onCreateViewHolder(
                LayoutInflater.from(parent.context),
                parent,
            )
                ?: return MyViewHolder(
                    ItemCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                )

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    open fun getRealDataPosition(position: Int): Int = position

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
    ) {
        val realItemPosition = getRealDataPosition(position)
        val item = getItem(realItemPosition) ?: return

        // If the sum of consecutive two items of a RecyclerView is not greater than
        // the ImageCarousel view width, then the scrollToPosition() method will not work
        // as expected. So we will check the width of the element and increase the minimum width
        // for fixing the problem if autoWidthFixing is true.
        if (autoWidthFixing && carouselType == CarouselType.SHOWCASE) {
            val containerWidth = recyclerView.width
            if (holder.itemView.layoutParams.width >= 0 &&
                holder.itemView.layoutParams.width * 2 <= containerWidth
            ) {
                holder.itemView.layoutParams.width = (containerWidth / 2) + 1
            }
        }

        // Init views
        if (holder.binding is ItemCarouselBinding) {
            holder.binding.img.scaleType = imageScaleType

            if (imagePlaceholder != null) {
                holder.binding.img.setImage(item, imagePlaceholder)
            } else {
                holder.binding.img.setImage(item)
            }

            listener?.apply {
                holder.itemView.setOnClickListener {
                    this.onClick(realItemPosition, item)
                }

                holder.itemView.setOnLongClickListener {
                    this.onLongClick(realItemPosition, item)

                    true
                }
            }
        }

        // Init listeners
        listener?.onBindViewHolder(
            holder.binding,
            item,
            realItemPosition,
        )

        // Init start and end offsets
        if (carouselType == CarouselType.SHOWCASE) {
            holder.itemView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (recyclerView.itemDecorationCount > 0) {
                            recyclerView.removeItemDecorationAt(0)
                        }

                        if (carouselGravity == CarouselGravity.START) {
                            recyclerView.addItemDecoration(
                                CarouselItemDecoration(
                                    0,
                                    0,
                                ),
                                0,
                            )
                        } else {
                            recyclerView.addItemDecoration(
                                CarouselItemDecoration(
                                    holder.itemView.width,
                                    0,
                                ),
                                0,
                            )
                        }

                        holder.itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                },
            )
        }
    }

    open fun getItem(position: Int): CarouselItem? =
        if (position < dataList.size) {
            dataList[position]
        } else {
            null
        }

    /**
     * Clear previous items and add all given carousel items.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(newDataList: List<CarouselItem>): List<CarouselItem> {
        this.dataList.clear()

        this.dataList.addAll(newDataList)
        notifyDataSetChanged()

        return this.dataList
    }

    /**
     * Append all given carousel items with the existing items.
     */
    fun appendData(newDataList: List<CarouselItem>): List<CarouselItem> {
        val oldSize = this.dataList.size

        this.dataList.addAll(newDataList)
        notifyItemRangeInserted(oldSize, newDataList.size)

        return this.dataList
    }

    /**
     * Add a single carousel item with the existing items.
     */
    fun appendData(item: CarouselItem): List<CarouselItem> {
        this.dataList.add(item)
        notifyItemInserted(dataList.size - 1)

        return this.dataList
    }
}


package com.example.imagecrouse.ui.whynotimagecarousel.adapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagecrouse.ui.whynotimagecarousel.ImageCarousel
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselGravity
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselItem
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselType

class InfiniteCarouselAdapter(
    recyclerView: RecyclerView,
    carouselType: CarouselType,
    carouselGravity: CarouselGravity,
    autoWidthFixing: Boolean,
    imageScaleType: ImageView.ScaleType,
    imagePlaceholder: Drawable?,
) : FiniteCarouselAdapter(
        recyclerView,
        carouselType,
        carouselGravity,
        autoWidthFixing,
        imageScaleType,
        imagePlaceholder,
    ) {
    override fun getItemCount(): Int = if (dataList.isEmpty()) 0 else Integer.MAX_VALUE

    override fun getItem(position: Int): CarouselItem? =
        if (position < itemCount) {
            dataList[position % dataList.size]
        } else {
            null
        }

    override fun getRealDataPosition(position: Int): Int {
        if (dataList.isEmpty()) return ImageCarousel.NO_POSITION
        return position % dataList.size
    }
}

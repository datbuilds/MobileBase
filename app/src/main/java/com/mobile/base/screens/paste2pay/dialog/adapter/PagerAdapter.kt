package com.mobile.base.screens.paste2pay.dialog.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobile.base.databinding.ItemAiIntroBinding
import com.mobile.base.screens.paste2pay.dialog.OnAIChartEntity
import com.mobile.base.utils.extensions.inflater

class PagerAdapter(private val onboards: List<OnAIChartEntity>) :
    RecyclerView.Adapter<PagerAdapter.ViewHolder>() {

    private var binding: ItemAiIntroBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAiIntroBinding.inflate(parent.inflater(), parent, false)
        return ViewHolder(binding!!)
    }

    override fun getItemCount() = onboards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(onboards[position])
    }

    fun getBindingView() = binding

    class ViewHolder(val binding: ItemAiIntroBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onboard: OnAIChartEntity) {
            binding.root.context.apply {
                binding.ivStep.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        onboard.imageRes
                    )
                )
            }
        }
    }
}
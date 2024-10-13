package com.on.staccato.presentation.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemVisitsBinding
import com.on.staccato.presentation.memory.MemoryHandler
import com.on.staccato.presentation.memory.model.MemoryStaccatoUiModel

class VisitsAdapter(private val handler: MemoryHandler) : ListAdapter<MemoryStaccatoUiModel, StaccatoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StaccatoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVisitsBinding.inflate(inflater, parent, false)
        return StaccatoViewHolder(binding, handler)
    }

    override fun onBindViewHolder(
        holder: StaccatoViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateVisits(visits: List<MemoryStaccatoUiModel>) {
        submitList(visits)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MemoryStaccatoUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MemoryStaccatoUiModel,
                    newItem: MemoryStaccatoUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: MemoryStaccatoUiModel,
                    newItem: MemoryStaccatoUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

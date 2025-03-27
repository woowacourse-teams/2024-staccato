package com.on.staccato.presentation.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemStaccatosBinding
import com.on.staccato.presentation.memory.MemoryHandler
import com.on.staccato.presentation.memory.model.MemoryStaccatoUiModel

class StaccatosAdapter(private val handler: MemoryHandler) : ListAdapter<MemoryStaccatoUiModel, StaccatoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StaccatoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStaccatosBinding.inflate(inflater, parent, false)
        return StaccatoViewHolder(binding, handler)
    }

    override fun onBindViewHolder(
        holder: StaccatoViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateStaccatos(staccatos: List<MemoryStaccatoUiModel>) {
        submitList(staccatos)
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

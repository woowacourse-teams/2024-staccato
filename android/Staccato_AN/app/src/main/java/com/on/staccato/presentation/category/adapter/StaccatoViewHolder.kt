package com.on.staccato.presentation.category.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.databinding.ItemStaccatosBinding
import com.on.staccato.presentation.category.MemoryHandler
import com.on.staccato.presentation.category.model.MemoryStaccatoUiModel

class StaccatoViewHolder(
    private val binding: ItemStaccatosBinding,
    private val handler: MemoryHandler,
) : ViewHolder(binding.root) {
    fun bind(memoryStaccato: MemoryStaccatoUiModel) {
        binding.staccato = memoryStaccato
        binding.handler = handler
    }
}

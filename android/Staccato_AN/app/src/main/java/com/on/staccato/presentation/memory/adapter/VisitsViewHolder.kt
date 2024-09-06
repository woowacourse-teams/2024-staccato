package com.on.staccato.presentation.memory.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.databinding.ItemVisitsBinding
import com.on.staccato.presentation.memory.MemoryHandler
import com.on.staccato.presentation.memory.model.MemoryVisitUiModel

class VisitsViewHolder(
    private val binding: ItemVisitsBinding,
    private val handler: MemoryHandler,
) : ViewHolder(binding.root) {
    fun bind(memoryVisit: MemoryVisitUiModel) {
        binding.visit = memoryVisit
        binding.handler = handler
    }
}

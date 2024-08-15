package com.woowacourse.staccato.presentation.Memory.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woowacourse.staccato.databinding.ItemVisitsBinding
import com.woowacourse.staccato.presentation.memory.MemoryHandler
import com.woowacourse.staccato.presentation.memory.model.MemoryVisitUiModel

class VisitsViewHolder(
    private val binding: ItemVisitsBinding,
    private val handler: MemoryHandler,
) : ViewHolder(binding.root) {
    fun bind(MemoryVisit: MemoryVisitUiModel) {
        binding.visit = MemoryVisit
        binding.handler = handler
    }
}

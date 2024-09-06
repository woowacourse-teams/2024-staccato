package com.on.staccato.presentation.timeline.adapter

import com.on.staccato.databinding.ItemTimelineLastBinding
import com.on.staccato.presentation.timeline.TimelineHandler
import com.on.staccato.presentation.timeline.model.TimelineUiModel

class LastTimelineViewHolder(
    private val binding: ItemTimelineLastBinding,
    private val eventHandler: TimelineHandler,
) : TimelineViewHolder(binding, eventHandler) {
    override fun bind(item: TimelineUiModel) {
        binding.memory = item
        binding.eventHandler = eventHandler
    }
}

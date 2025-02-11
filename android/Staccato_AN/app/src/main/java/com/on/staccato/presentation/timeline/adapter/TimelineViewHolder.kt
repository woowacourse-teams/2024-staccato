package com.on.staccato.presentation.timeline.adapter

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemTimelineBinding
import com.on.staccato.presentation.timeline.TimelineHandler
import com.on.staccato.presentation.timeline.model.TimelineUiModel

class TimelineViewHolder(
    private val binding: ItemTimelineBinding,
    private val eventHandler: TimelineHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: TimelineUiModel) {
        binding.category = item
        binding.eventHandler = eventHandler
    }
}

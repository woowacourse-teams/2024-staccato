package com.on.staccato.presentation.timeline.adapter

import android.view.View
import com.on.staccato.databinding.ItemTimelineFirstBinding
import com.on.staccato.presentation.timeline.TimelineHandler
import com.on.staccato.presentation.timeline.model.TimelineUiModel

class FirstTimelineViewHolder(
    private val binding: ItemTimelineFirstBinding,
    private val eventHandler: TimelineHandler,
    private val isOnlyOne: Boolean,
) : TimelineViewHolder(binding, eventHandler) {
    override fun bind(item: TimelineUiModel) {
        binding.memory = item
        binding.eventHandler = eventHandler
        binding.viewTimeline.visibility =
            if (isOnlyOne) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
    }
}

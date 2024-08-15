package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.ItemTimelineFirstBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.model.TimelineMemoryUiModel

class FirstMemoryViewHolder(
    private val binding: ItemTimelineFirstBinding,
    private val eventHandler: TimelineHandler,
) : TimelineViewHolder(binding, eventHandler) {
    override fun bind(item: TimelineMemoryUiModel) {
        binding.memory = item
        binding.eventHandler = eventHandler
    }
}

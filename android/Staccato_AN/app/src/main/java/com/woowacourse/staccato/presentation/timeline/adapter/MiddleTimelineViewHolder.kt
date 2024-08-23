package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.ItemTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.model.TimelineUiModel

class MiddleTimelineViewHolder(
    private val binding: ItemTimelineMiddleBinding,
    private val eventHandler: TimelineHandler,
) : TimelineViewHolder(binding, eventHandler) {
    override fun bind(item: TimelineUiModel) {
        binding.memory = item
        binding.eventHandler = eventHandler
    }
}

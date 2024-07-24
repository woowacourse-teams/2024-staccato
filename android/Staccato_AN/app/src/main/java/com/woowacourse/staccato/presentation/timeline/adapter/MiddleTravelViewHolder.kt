package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class MiddleTravelViewHolder(
    private val binding: LayoutItemFragmentTimelineMiddleBinding,
    private val eventHandler: TimelineHandler,
): TimelineViewHolder(binding, eventHandler) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
        binding.eventHandler = eventHandler
    }
}

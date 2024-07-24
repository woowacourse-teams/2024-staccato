package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class MiddleTravelViewHolder(
    private val binding: LayoutItemFragmentTimelineMiddleBinding
): TimelineViewHolder(binding) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
    }
}

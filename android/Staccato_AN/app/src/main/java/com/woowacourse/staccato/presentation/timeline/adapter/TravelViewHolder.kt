package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class TravelViewHolder(
    private val binding: LayoutItemFragmentTimelineBinding
): TimelineViewHolder(binding) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
    }
}

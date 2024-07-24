package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineFirstBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class FirstTravelViewHolder(
    private val binding: LayoutItemFragmentTimelineFirstBinding
): TimelineViewHolder(binding) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
    }
}

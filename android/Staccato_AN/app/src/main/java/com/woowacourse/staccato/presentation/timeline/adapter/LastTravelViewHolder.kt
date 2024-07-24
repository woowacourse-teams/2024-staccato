package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineLastBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class LastTravelViewHolder(
    private val binding: LayoutItemFragmentTimelineLastBinding
): TimelineViewHolder(binding) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
    }
}

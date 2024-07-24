package com.woowacourse.staccato.presentation.timeline.adapter

import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineNoThumbnailBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class WithoutThumbnailTravelViewHolder(
    private val binding: LayoutItemFragmentTimelineNoThumbnailBinding,
): TimelineViewHolder(binding) {
    override fun bind(item: TimelineTravelUiModel) {
        binding.travel = item
    }
}

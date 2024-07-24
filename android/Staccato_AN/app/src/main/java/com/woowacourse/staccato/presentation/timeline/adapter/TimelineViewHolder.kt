package com.woowacourse.staccato.presentation.timeline.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

abstract class TimelineViewHolder(
    binding: ViewDataBinding,
    private val eventHandler: TimelineHandler,
) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: TimelineTravelUiModel)
}

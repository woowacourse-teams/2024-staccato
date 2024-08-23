package com.woowacourse.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemTimelineFirstBinding
import com.woowacourse.staccato.databinding.ItemTimelineLastBinding
import com.woowacourse.staccato.databinding.ItemTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.model.TimelineUiModel

class TimelineAdapter(private val eventHandler: TimelineHandler) :
    ListAdapter<TimelineUiModel, TimelineViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int {
        return TimelineViewType.fromPosition(position, itemCount).viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TimelineViewHolder {
        return when (TimelineViewType.byViewType(viewType)) {
            TimelineViewType.FIRST_ITEM -> {
                val binding =
                    ItemTimelineFirstBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                FirstTimelineViewHolder(binding, eventHandler, currentList.size == LIST_SIZE_ONE)
            }

            TimelineViewType.MIDDLE_ITEM -> {
                val binding =
                    ItemTimelineMiddleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                MiddleTimelineViewHolder(binding, eventHandler)
            }

            TimelineViewType.LAST_ITEM -> {
                val binding =
                    ItemTimelineLastBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                LastTimelineViewHolder(binding, eventHandler)
            }
        }
    }

    override fun onBindViewHolder(
        holder: TimelineViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    fun updateTimeline(newTimeline: List<TimelineUiModel>) {
        submitList(newTimeline)
    }

    companion object {
        private const val LIST_SIZE_ONE = 1

        val diffUtil =
            object : DiffUtil.ItemCallback<TimelineUiModel>() {
                override fun areContentsTheSame(
                    oldItem: TimelineUiModel,
                    newItem: TimelineUiModel,
                ): Boolean = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: TimelineUiModel,
                    newItem: TimelineUiModel,
                ): Boolean = oldItem.memoryId == newItem.memoryId
            }
    }
}

package com.woowacourse.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemTimelineFirstBinding
import com.woowacourse.staccato.databinding.ItemTimelineLastBinding
import com.woowacourse.staccato.databinding.ItemTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.model.TimelineMemoryUiModel

class TimelineAdapter(private val eventHandler: TimelineHandler) :
    ListAdapter<TimelineMemoryUiModel, TimelineViewHolder>(diffUtil) {
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
                FirstMemoryViewHolder(binding, eventHandler)
            }

            TimelineViewType.MIDDLE_ITEM -> {
                val binding =
                    ItemTimelineMiddleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                MiddleMemoryViewHolder(binding, eventHandler)
            }

            TimelineViewType.LAST_ITEM -> {
                val binding =
                    ItemTimelineLastBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                LastMemoryViewHolder(binding, eventHandler)
            }
        }
    }

    override fun onBindViewHolder(
        holder: TimelineViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    fun updateTimeline(newMemories: List<TimelineMemoryUiModel>) {
        submitList(newMemories)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<TimelineMemoryUiModel>() {
                override fun areContentsTheSame(
                    oldItem: TimelineMemoryUiModel,
                    newItem: TimelineMemoryUiModel,
                ): Boolean = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: TimelineMemoryUiModel,
                    newItem: TimelineMemoryUiModel,
                ): Boolean = oldItem.memoryId == newItem.memoryId
            }
    }
}

package com.on.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemTimelineBinding
import com.on.staccato.presentation.timeline.TimelineHandler
import com.on.staccato.presentation.timeline.model.TimelineUiModel

class TimelineAdapter(private val eventHandler: TimelineHandler) :
    ListAdapter<TimelineUiModel, TimelineViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TimelineViewHolder {
        val binding =
            ItemTimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return TimelineViewHolder(binding, eventHandler)
    }

    override fun onBindViewHolder(
        holder: TimelineViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    fun updateTimeline(
        newTimeline: List<TimelineUiModel>,
        commitCallback: Runnable?,
    ) {
        submitList(newTimeline, commitCallback)
    }

    companion object {
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

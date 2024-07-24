package com.woowacourse.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineFirstBinding
import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineLastBinding
import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class TimelineAdapter() : RecyclerView.Adapter<TimelineViewHolder>() {
    private var travels = emptyList<TimelineTravelUiModel>()

    override fun getItemViewType(position: Int): Int {
        return TimelineViewType.fromPosition(position, itemCount).ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return when (viewType) {
            TimelineViewType.FIRST_ITEM.ordinal -> {
                val binding =
                    LayoutItemFragmentTimelineFirstBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                FirstTravelViewHolder(binding)
            }

            TimelineViewType.MIDDLE_ITEM.ordinal -> {
                val binding =
                    LayoutItemFragmentTimelineMiddleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                MiddleTravelViewHolder(binding)
            }

            else -> {
                val binding =
                    LayoutItemFragmentTimelineLastBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                LastTravelViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = travels.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(travels[position])
    }

    fun setTravels(newTravels: List<TimelineTravelUiModel>) {
        travels = newTravels
        notifyItemRangeInserted(0, newTravels.size)
    }
}

package com.woowacourse.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineBinding
import com.woowacourse.staccato.databinding.LayoutItemFragmentTimelineNoThumbnailBinding
import com.woowacourse.staccato.presentation.timeline.TimelineTravelUiModel

class TimelineAdapter() : RecyclerView.Adapter<TimelineViewHolder>() {
    private var travels = emptyList<TimelineTravelUiModel>()

    override fun getItemViewType(position: Int): Int {
        return TimelineViewType.from(travels[position].travelThumbnail).ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return when (viewType) {
            TimelineViewType.NO_THUMBNAIL.ordinal -> {
                val binding =
                    LayoutItemFragmentTimelineNoThumbnailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                WithoutThumbnailTravelViewHolder(binding)
            }

            TimelineViewType.WITH_THUMBNAIL.ordinal -> {
                val binding =
                    LayoutItemFragmentTimelineBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                TravelViewHolder(binding)
            }

            else -> {
                val binding =
                    LayoutItemFragmentTimelineNoThumbnailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                WithoutThumbnailTravelViewHolder(binding)
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

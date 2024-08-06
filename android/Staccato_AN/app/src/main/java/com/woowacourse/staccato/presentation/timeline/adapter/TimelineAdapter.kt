package com.woowacourse.staccato.presentation.timeline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemTimelineFirstBinding
import com.woowacourse.staccato.databinding.ItemTimelineLastBinding
import com.woowacourse.staccato.databinding.ItemTimelineMiddleBinding
import com.woowacourse.staccato.presentation.timeline.TimelineHandler
import com.woowacourse.staccato.presentation.timeline.model.TimelineTravelUiModel

class TimelineAdapter(private val eventHandler: TimelineHandler) :
    RecyclerView.Adapter<TimelineViewHolder>() {
    private var travels = emptyList<TimelineTravelUiModel>()

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
                FirstTravelViewHolder(binding, eventHandler)
            }

            TimelineViewType.MIDDLE_ITEM -> {
                val binding =
                    ItemTimelineMiddleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                MiddleTravelViewHolder(binding, eventHandler)
            }

            TimelineViewType.LAST_ITEM -> {
                val binding =
                    ItemTimelineLastBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                LastTravelViewHolder(binding, eventHandler)
            }
        }
    }

    override fun getItemCount(): Int = travels.size

    override fun onBindViewHolder(
        holder: TimelineViewHolder,
        position: Int,
    ) {
        holder.bind(travels[position])
    }

    fun setTravels(newTravels: List<TimelineTravelUiModel>) {
        if (travels.isEmpty()) {
            travels = newTravels
            notifyItemRangeInserted(0, newTravels.size)
        }
    }
}
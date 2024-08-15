package com.woowacourse.staccato.presentation.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemVisitsBinding
import com.woowacourse.staccato.presentation.memory.TravelHandler
import com.woowacourse.staccato.presentation.memory.model.TravelVisitUiModel

class VisitsAdapter(private val handler: TravelHandler) : ListAdapter<TravelVisitUiModel, VisitsViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VisitsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVisitsBinding.inflate(inflater, parent, false)
        return VisitsViewHolder(binding, handler)
    }

    override fun onBindViewHolder(
        holder: VisitsViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateVisits(visits: List<TravelVisitUiModel>) {
        submitList(visits)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<TravelVisitUiModel>() {
                override fun areItemsTheSame(
                    oldItem: TravelVisitUiModel,
                    newItem: TravelVisitUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: TravelVisitUiModel,
                    newItem: TravelVisitUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

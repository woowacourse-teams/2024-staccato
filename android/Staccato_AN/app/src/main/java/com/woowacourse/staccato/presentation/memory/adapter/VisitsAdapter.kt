package com.woowacourse.staccato.presentation.Memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemVisitsBinding
import com.woowacourse.staccato.presentation.memory.MemoryHandler
import com.woowacourse.staccato.presentation.memory.model.MemoryVisitUiModel

class VisitsAdapter(private val handler: MemoryHandler) : ListAdapter<MemoryVisitUiModel, VisitsViewHolder>(diffUtil) {
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

    fun updateVisits(visits: List<MemoryVisitUiModel>) {
        submitList(visits)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MemoryVisitUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MemoryVisitUiModel,
                    newItem: MemoryVisitUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: MemoryVisitUiModel,
                    newItem: MemoryVisitUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

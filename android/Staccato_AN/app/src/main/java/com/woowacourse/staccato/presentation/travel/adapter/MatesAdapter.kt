package com.woowacourse.staccato.presentation.travel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemMatesBinding
import com.woowacourse.staccato.presentation.travel.model.MateUiModel

class MatesAdapter : ListAdapter<MateUiModel, MatesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MatesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMatesBinding.inflate(inflater, parent, false)
        return MatesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MatesViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateMates(mates: List<MateUiModel>) {
        submitList(mates)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MateUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MateUiModel,
                    newItem: MateUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: MateUiModel,
                    newItem: MateUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

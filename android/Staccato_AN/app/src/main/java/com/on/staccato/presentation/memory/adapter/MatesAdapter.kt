package com.on.staccato.presentation.memory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemMatesBinding
import com.on.staccato.presentation.common.MemberUiModel

class MatesAdapter : ListAdapter<MemberUiModel, MatesViewHolder>(diffUtil) {
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

    fun updateMates(mates: List<MemberUiModel>) {
        submitList(mates)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MemberUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MemberUiModel,
                    newItem: MemberUiModel,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: MemberUiModel,
                    newItem: MemberUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}

package com.on.staccato.presentation.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.databinding.ItemMatesBinding
import com.on.staccato.domain.model.Member

class MatesAdapter : ListAdapter<Member, MatesViewHolder>(diffUtil) {
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

    fun updateMates(mates: List<Member>) {
        submitList(mates)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<Member>() {
                override fun areItemsTheSame(
                    oldItem: Member,
                    newItem: Member,
                ): Boolean = oldItem.memberId == newItem.memberId

                override fun areContentsTheSame(
                    oldItem: Member,
                    newItem: Member,
                ): Boolean = oldItem == newItem
            }
    }
}

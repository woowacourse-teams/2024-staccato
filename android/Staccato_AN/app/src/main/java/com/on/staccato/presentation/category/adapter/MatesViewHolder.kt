package com.on.staccato.presentation.category.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.databinding.ItemMatesBinding
import com.on.staccato.domain.model.Member

class MatesViewHolder(
    private val binding: ItemMatesBinding,
) : ViewHolder(binding.root) {
    fun bind(mate: Member) {
        binding.mate = mate
    }
}

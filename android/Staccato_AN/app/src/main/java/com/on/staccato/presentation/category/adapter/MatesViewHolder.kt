package com.on.staccato.presentation.category.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.databinding.ItemMatesBinding
import com.on.staccato.presentation.common.MemberUiModel

class MatesViewHolder(
    private val binding: ItemMatesBinding,
) : ViewHolder(binding.root) {
    fun bind(mate: MemberUiModel) {
        binding.mate = mate
    }
}

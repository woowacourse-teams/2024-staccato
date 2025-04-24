package com.on.staccato.presentation.common.color.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.databinding.ItemColorSelectionBinding
import com.on.staccato.presentation.common.color.CategoryColor

class ColorSelectionViewHolder(
    private val binding: ItemColorSelectionBinding,
    private val handler: ColorSelectionHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: CategoryColor,
        isSelected: Boolean,
    ) {
        binding.handler = handler
        binding.categoryColor = item
        binding.isSelected = isSelected
    }
}

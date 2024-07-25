package com.woowacourse.staccato.presentation.travel.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woowacourse.staccato.databinding.ItemMatesBinding
import com.woowacourse.staccato.presentation.travel.model.MateUiModel

class MatesViewHolder(
    private val binding: ItemMatesBinding,
) : ViewHolder(binding.root) {
    fun bind(mate: MateUiModel) {
        binding.mate = mate
    }
}

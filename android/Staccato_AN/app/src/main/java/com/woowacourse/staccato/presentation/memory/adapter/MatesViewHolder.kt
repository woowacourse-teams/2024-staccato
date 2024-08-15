package com.woowacourse.staccato.presentation.memory.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woowacourse.staccato.databinding.ItemMatesBinding
import com.woowacourse.staccato.presentation.common.MemberUiModel

class MatesViewHolder(
    private val binding: ItemMatesBinding,
) : ViewHolder(binding.root) {
    fun bind(mate: MemberUiModel) {
        binding.mate = mate
    }
}

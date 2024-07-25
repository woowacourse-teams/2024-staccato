package com.woowacourse.staccato.presentation.travel.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woowacourse.staccato.databinding.ItemVisitsBinding
import com.woowacourse.staccato.presentation.travel.TravelHandler
import com.woowacourse.staccato.presentation.travel.model.VisitUiModel

class VisitsViewHolder(
    private val binding: ItemVisitsBinding,
    private val handler: TravelHandler,
) : ViewHolder(binding.root) {
    fun bind(visit: VisitUiModel) {
        binding.visit = visit
        binding.handler = handler
    }
}

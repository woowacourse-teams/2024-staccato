package com.woowacourse.staccato.presentation.travel.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.woowacourse.staccato.databinding.ItemVisitsBinding
import com.woowacourse.staccato.presentation.travel.TravelHandler
import com.woowacourse.staccato.presentation.travel.model.TravelVisitUiModel

class VisitsViewHolder(
    private val binding: ItemVisitsBinding,
    private val handler: TravelHandler,
) : ViewHolder(binding.root) {
    fun bind(travelVisit: TravelVisitUiModel) {
        binding.visit = travelVisit
        binding.handler = handler
    }
}

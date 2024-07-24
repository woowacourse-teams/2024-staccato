package com.woowacourse.staccato.presentation.visit.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemMyVisitLogBinding
import com.woowacourse.staccato.databinding.ItemVisitDefaultBinding
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel

sealed class VisitViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    class VisitDefaultViewHolder(private val binding: ItemVisitDefaultBinding) :
        VisitViewHolder(binding) {
        fun bind(item: VisitDetailUiModel.VisitDefaultUiModel) {
            binding.visitDefault = item
        }
    }

    class MyVisitLogViewHolder(private val binding: ItemMyVisitLogBinding) :
        VisitViewHolder(binding) {
        fun bind(item: VisitDetailUiModel.VisitLogUiModel) {
            binding.visitLog = item
        }
    }
}

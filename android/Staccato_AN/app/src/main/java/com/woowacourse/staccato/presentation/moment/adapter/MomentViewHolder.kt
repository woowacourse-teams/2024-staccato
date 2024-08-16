package com.woowacourse.staccato.presentation.moment.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemMyVisitLogBinding
import com.woowacourse.staccato.databinding.ItemVisitDefaultBinding
import com.woowacourse.staccato.presentation.moment.model.MomentDetailUiModel

sealed class MomentViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    class MomentDefaultViewHolder(private val binding: ItemVisitDefaultBinding) :
        MomentViewHolder(binding) {
        val adapter = HorizontalPhotoAdapter()

        fun bind(item: MomentDetailUiModel.MomentDefaultUiModel) {
            binding.visitDefault = item
            val urls = item.momentImageUrls
            adapter.submitList(urls)
            binding.rvPhotoHorizontal.adapter = adapter
        }
    }

    class MyCommentViewHolder(private val binding: ItemMyVisitLogBinding) :
        MomentViewHolder(binding) {
        fun bind(item: MomentDetailUiModel.VisitLogUiModel) {
            binding.visitLog = item
        }
    }
}

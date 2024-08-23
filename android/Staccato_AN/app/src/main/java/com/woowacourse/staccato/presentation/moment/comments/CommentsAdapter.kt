package com.woowacourse.staccato.presentation.moment.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.staccato.databinding.ItemMomentMyCommentBinding

class CommentsAdapter(private val commentHandler: CommentHandler) :
    ListAdapter<CommentUiModel, CommentViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentViewHolder {
        val binding =
            ItemMomentMyCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return CommentViewHolder(binding, commentHandler)
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    fun updateComments(updatedComments: List<CommentUiModel>) {
        submitList(updatedComments)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<CommentUiModel>() {
                override fun areContentsTheSame(
                    oldItem: CommentUiModel,
                    newItem: CommentUiModel,
                ): Boolean = oldItem == newItem

                override fun areItemsTheSame(
                    oldItem: CommentUiModel,
                    newItem: CommentUiModel,
                ): Boolean = oldItem.id == newItem.id
            }
    }
}

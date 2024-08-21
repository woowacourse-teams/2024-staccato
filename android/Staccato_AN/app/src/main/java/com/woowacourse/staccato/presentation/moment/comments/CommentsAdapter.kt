package com.woowacourse.staccato.presentation.moment.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.databinding.ItemMomentMyCommentBinding

class CommentsAdapter :
    ListAdapter<CommentUiModel, CommentsAdapter.CommentViewHolder>(diffUtil) {
    // Todo: 추후 나의 댓글, 다른 사용자의 댓글 구분하기
    class CommentViewHolder(private val binding: ItemMomentMyCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commentUiModel: CommentUiModel) {
            binding.myComment = commentUiModel
        }
    }

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
        // Todo: 롱 클릭 이벤트 바인딩 적용하기
        return CommentViewHolder(binding)
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

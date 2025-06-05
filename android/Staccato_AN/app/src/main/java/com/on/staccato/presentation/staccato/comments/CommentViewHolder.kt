package com.on.staccato.presentation.staccato.comments

import android.view.Gravity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.databinding.ItemStaccatoMyCommentBinding
import com.on.staccato.databinding.ItemStaccatoOthersCommentBinding

sealed class CommentViewHolder(binding: ViewDataBinding) : ViewHolder(binding.root) {
    abstract val binding: ViewDataBinding

    class MyCommentViewHolder(
        override val binding: ItemStaccatoMyCommentBinding,
        private val handler: CommentHandler,
    ) : CommentViewHolder(binding) {
        fun bind(commentUiModel: CommentUiModel) {
            binding.myComment = commentUiModel
            binding.handler = handler
            binding.root.setOnLongClickListener {
                handler.onCommentLongClicked(
                    view = binding.tvStaccatoMyCommentChat,
                    gravity = Gravity.END,
                    id = commentUiModel.id,
                )
                false
            }
        }
    }

    class OtherCommentViewHolder(
        override val binding: ItemStaccatoOthersCommentBinding,
        private val handler: CommentHandler,
    ) : CommentViewHolder(binding) {
        fun bind(commentUiModel: CommentUiModel) {
            binding.othersComment = commentUiModel
            binding.handler = handler
            binding.root.setOnLongClickListener {
                handler.onCommentLongClicked(
                    view = binding.tvStaccatoOthersCommentChat,
                    gravity = Gravity.START,
                    id = commentUiModel.id,
                )
                false
            }
        }
    }
}

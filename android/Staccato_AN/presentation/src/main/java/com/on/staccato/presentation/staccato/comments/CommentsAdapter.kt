package com.on.staccato.presentation.staccato.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.staccato.presentation.databinding.ItemStaccatoMyCommentBinding
import com.on.staccato.presentation.databinding.ItemStaccatoOthersCommentBinding
import com.on.staccato.presentation.staccato.comments.CommentViewHolder.MyCommentViewHolder
import com.on.staccato.presentation.staccato.comments.CommentViewHolder.OtherCommentViewHolder
import com.on.staccato.presentation.staccato.comments.CommentViewType.MY_COMMENT
import com.on.staccato.presentation.staccato.comments.CommentViewType.OTHER_COMMENT

class CommentsAdapter(
    private val commentHandler: CommentHandler,
) : ListAdapter<CommentUiModel, CommentViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int =
        if (currentList[position].isMine) {
            MY_COMMENT.viewType
        } else {
            OTHER_COMMENT.viewType
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (CommentViewType.from(viewType)) {
            MY_COMMENT -> {
                val binding = ItemStaccatoMyCommentBinding.inflate(inflater, parent, false)
                MyCommentViewHolder(binding, commentHandler)
            }
            OTHER_COMMENT -> {
                val binding = ItemStaccatoOthersCommentBinding.inflate(inflater, parent, false)
                OtherCommentViewHolder(binding, commentHandler)
            }
        }
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int,
    ) {
        when (getItemViewType(position)) {
            MY_COMMENT.viewType -> (holder as MyCommentViewHolder).bind(currentList[position])
            OTHER_COMMENT.viewType -> (holder as OtherCommentViewHolder).bind(currentList[position])
        }
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

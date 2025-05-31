package com.on.staccato.presentation.staccato.comments

import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.on.staccato.R
import com.on.staccato.databinding.ItemStaccatoMyCommentBinding
import com.on.staccato.databinding.ItemStaccatoOthersCommentBinding
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.common.DialogHandler

sealed class CommentViewHolder(binding: ViewDataBinding) : ViewHolder(binding.root), DialogHandler {
    abstract val binding: ViewDataBinding
    private val deleteDialogFragment = DeleteDialogFragment { onConfirmClicked() }

    class MyCommentViewHolder(
        override val binding: ItemStaccatoMyCommentBinding,
        private val commentHandler: CommentHandler,
    ) : CommentViewHolder(binding) {
        override fun onConfirmClicked() {
            binding.myComment?.id?.let { commentHandler.onDeleteButtonClicked(it) }
        }

        fun bind(commentUiModel: CommentUiModel) {
            binding.myComment = commentUiModel
            binding.root.setOnLongClickListener {
                onCommentLongClicked()
                false
            }
        }
    }

    class OtherCommentViewHolder(
        override val binding: ItemStaccatoOthersCommentBinding,
        private val commentHandler: CommentHandler,
    ) : CommentViewHolder(binding) {
        override fun onConfirmClicked() {
            binding.othersComment?.id?.let { commentHandler.onDeleteButtonClicked(it) }
        }

        fun bind(commentUiModel: CommentUiModel) {
            binding.othersComment = commentUiModel
            binding.root.setOnLongClickListener {
                onCommentLongClicked()
                false
            }
        }
    }

    fun onCommentLongClicked() {
        val popup = inflateCreationMenu(binding.root)
        setUpCreationMenu(popup)
        popup.show()
    }

    private fun inflateCreationMenu(view: View): PopupMenu {
        val popup =
            PopupMenu(view.context, view, Gravity.END, 0, R.style.Theme_Staccato_AN_PopupMenu)
        popup.menuInflater.inflate(R.menu.menu_comment, popup.menu)
        return popup
    }

    private fun setUpCreationMenu(popup: PopupMenu) {
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.comment_delete ->
                    deleteDialogFragment.show(
                        FragmentManager.findFragmentManager(binding.root),
                        DeleteDialogFragment.TAG,
                    )
            }
            false
        }
    }
}

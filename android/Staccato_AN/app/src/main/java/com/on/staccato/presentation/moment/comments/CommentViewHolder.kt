package com.on.staccato.presentation.moment.comments

import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.on.staccato.R
import com.on.staccato.databinding.ItemMomentMyCommentBinding

// Todo: 추후 나의 댓글, 다른 사용자의 댓글 구분하기
class CommentViewHolder(
    private val binding: ItemMomentMyCommentBinding,
    private val commentHandler: CommentHandler,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(commentUiModel: CommentUiModel) {
        binding.myComment = commentUiModel
        binding.root.setOnLongClickListener {
            onCommentLongClicked(commentId = commentUiModel.id)
            false
        }
    }

    private fun onCommentLongClicked(commentId: Long) {
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
                R.id.comment_update ->
                    binding.myComment?.id?.let {
                        commentHandler.onUpdateButtonClicked(
                            it,
                        )
                    }

                R.id.comment_delete ->
                    binding.myComment?.id?.let {
                        commentHandler.onDeleteButtonClicked(
                            it,
                        )
                    }
            }
            false
        }
    }
}

package com.on.staccato.presentation.staccato.comments

interface CommentHandler {
    fun onSendButtonClicked()

    fun onUpdateButtonClicked(commentId: Long)

    fun onDeleteButtonClicked(commentId: Long)
}

package com.woowacourse.staccato.presentation.moment.comments

interface CommentHandler {
    fun onSendButtonClicked()

    fun onUpdateButtonClicked(commentId: Long)

    fun onDeleteButtonClicked(commentId: Long)
}

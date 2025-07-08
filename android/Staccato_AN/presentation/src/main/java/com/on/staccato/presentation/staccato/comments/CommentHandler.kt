package com.on.staccato.presentation.staccato.comments

import android.view.View

interface CommentHandler {
    fun onCommentLongClicked(
        view: View,
        gravity: Int,
        id: Long,
    )
}

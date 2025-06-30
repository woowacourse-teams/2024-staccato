package com.on.staccato.presentation.staccato.comments

data class CommentUiModel(
    val id: Long,
    val nickname: String,
    val memberImageUrl: String?,
    val content: String,
    val isMine: Boolean,
)

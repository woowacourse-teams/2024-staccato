package com.on.staccato.presentation.staccato.comments

data class CommentUiModel(
    val id: Long = 0,
    val memberId: Long = 0,
    val nickname: String,
    val memberImageUrl: String? = null,
    val content: String,
)

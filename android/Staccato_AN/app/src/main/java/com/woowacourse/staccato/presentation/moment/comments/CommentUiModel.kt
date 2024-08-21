package com.woowacourse.staccato.presentation.moment.comments

data class CommentUiModel(
        val id: Long = 0,
        val memberId: Long = 0,
        val nickname: String,
        val memberImageUrl: String,
        val content: String,
    )

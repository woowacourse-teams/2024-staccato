package com.woowacourse.staccato.domain.model

data class Comment(
    val commentId: Long,
    val memberId: Long,
    val nickname: String,
    val memberImageUrl: String,
    val content: String,
)

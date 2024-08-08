package com.woowacourse.staccato.domain.model

data class VisitLog(
    val visitLogId: Long,
    val memberId: Long,
    val nickname: String,
    val memberImageUrl: String,
    val content: String,
)

package com.woowacourse.staccato.domain.model

data class VisitLog(
    val visitLogId: Long,
    val memberId: Long,
    val nickName: String,
    val memberImage: String,
    val content: String,
)

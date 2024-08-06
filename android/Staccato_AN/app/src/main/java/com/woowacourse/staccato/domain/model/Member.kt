package com.woowacourse.staccato.domain.model

data class Member(
    val memberId: Long,
    val nickName: String,
    val memberImage: String? = null,
)

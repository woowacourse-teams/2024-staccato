package com.woowacourse.staccato.domain.model

data class Member(
    val memberId: Long,
    val nickname: String,
    val memberImage: String? = null,
)

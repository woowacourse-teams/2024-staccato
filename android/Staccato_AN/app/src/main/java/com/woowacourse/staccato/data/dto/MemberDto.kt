package com.woowacourse.staccato.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    @SerialName("memberId") val memberId: Long,
    @SerialName("nickName") val nickName: String,
    @SerialName("memberImage") val memberImage: String,
)

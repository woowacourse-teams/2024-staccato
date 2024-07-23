package com.woowacourse.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineMemberDto(
    @SerialName("memberId") val memberId: Long,
    @SerialName("memberImage") val memberImage: String,
)

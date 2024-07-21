package com.woowacourse.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineMemberDto(
    @SerialName("member_id") val memberId: Long,
    @SerialName("member_image") val memberImage: String,
)

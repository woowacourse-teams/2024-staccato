package com.woowacourse.staccato.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    @SerialName("member_id") val memberId: Long,
    @SerialName("nick_name") val nickName: String,
    @SerialName("member_image") val memberImage: String,
)

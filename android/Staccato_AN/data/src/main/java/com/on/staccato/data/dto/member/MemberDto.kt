package com.on.staccato.data.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    @SerialName("memberId") val memberId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("memberImageUrl") val memberImage: String? = null,
    // 추후 기본값 제거 예정
    @SerialName("status") val status: String = "none",
)

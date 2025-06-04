package com.on.staccato.data.dto.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: 사용자 프로필 조회 API V2 머지 되면 필드 추가
@Serializable
data class MemberProfileResponse(
    @SerialName("nickname") val nickname: String,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("code") val code: String,
)

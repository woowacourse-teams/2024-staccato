package com.on.staccato.data.dto.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyProfileResponse(
    @SerialName("nickname") val nickname: String,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("code") val code: String,
)

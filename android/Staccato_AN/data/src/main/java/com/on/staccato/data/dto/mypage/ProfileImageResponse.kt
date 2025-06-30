package com.on.staccato.data.dto.mypage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageResponse(
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
)

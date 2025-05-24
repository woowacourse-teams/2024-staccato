package com.on.staccato.data.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDto(
    @SerialName("memberId") val id: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("memberImageUrl") val imageUrl: String? = null,
    @SerialName("memberRole") val role: String,
)

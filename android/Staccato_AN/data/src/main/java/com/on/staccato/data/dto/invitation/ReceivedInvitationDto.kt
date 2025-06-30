package com.on.staccato.data.dto.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceivedInvitationDto(
    @SerialName("invitationId") val invitationId: Long,
    @SerialName("inviterId") val inviterId: Long,
    @SerialName("inviterNickname") val inviterNickname: String,
    @SerialName("inviterProfileImageUrl") val inviterProfile: String? = null,
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryTitle") val categoryTitle: String,
)

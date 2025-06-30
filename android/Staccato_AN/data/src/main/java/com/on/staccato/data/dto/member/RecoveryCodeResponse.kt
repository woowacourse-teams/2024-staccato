package com.on.staccato.data.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecoveryCodeResponse(
    @SerialName("memberId") val id: Long,
    @SerialName("token") val token: String,
)

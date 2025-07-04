package com.on.staccato.data.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NicknameLoginResponse(
    @SerialName("memberId") val id: Long,
    @SerialName("token") val token: String,
)

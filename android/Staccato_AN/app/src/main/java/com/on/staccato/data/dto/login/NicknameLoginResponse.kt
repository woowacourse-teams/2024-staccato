package com.on.staccato.data.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NicknameLoginResponse(
    @SerialName("token") val token: String,
)

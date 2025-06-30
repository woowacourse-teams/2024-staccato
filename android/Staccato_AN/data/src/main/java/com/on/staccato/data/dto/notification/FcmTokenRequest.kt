package com.on.staccato.data.dto.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    @SerialName("token") val token: String,
    @SerialName("deviceType") val deviceType: String = "ANDROID",
    @SerialName("deviceId") val deviceId: String,
)

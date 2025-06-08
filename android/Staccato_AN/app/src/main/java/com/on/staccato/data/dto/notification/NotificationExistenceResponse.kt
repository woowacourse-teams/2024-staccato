package com.on.staccato.data.dto.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationExistenceResponse(
    @SerialName("isExist") val isExist: Boolean,
)

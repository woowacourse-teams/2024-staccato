package com.on.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentCreationResponse(
    @SerialName("momentId") val momentId: Long,
)

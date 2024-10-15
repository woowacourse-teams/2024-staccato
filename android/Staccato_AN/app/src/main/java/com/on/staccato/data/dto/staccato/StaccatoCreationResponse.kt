package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoCreationResponse(
    @SerialName("momentId") val staccatoId: Long,
)

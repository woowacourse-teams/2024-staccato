package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoShareLinkResponse(
    @SerialName("staccatoId") val staccatoId: Long,
    @SerialName("shareLink") val shareLink: String,
)

package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeelingRequest(
    @SerialName("feeling") val feeling: String,
)

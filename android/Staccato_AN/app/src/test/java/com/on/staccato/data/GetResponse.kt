package com.on.staccato.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetResponse(
    @SerialName("id") val id: Long,
    @SerialName("content") val content: String,
)

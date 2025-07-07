package com.on.staccato.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostRequest(
    @SerialName("content") val content: String,
)

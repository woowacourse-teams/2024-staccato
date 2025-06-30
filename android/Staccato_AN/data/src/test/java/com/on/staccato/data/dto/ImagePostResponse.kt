package com.on.staccato.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagePostResponse(
    @SerialName("imageUrl") val imageUrl: String,
)

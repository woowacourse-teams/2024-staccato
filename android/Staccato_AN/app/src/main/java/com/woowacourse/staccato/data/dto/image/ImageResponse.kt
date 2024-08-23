package com.woowacourse.staccato.data.dto.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("imageUrl") val imageUrl: String,
)

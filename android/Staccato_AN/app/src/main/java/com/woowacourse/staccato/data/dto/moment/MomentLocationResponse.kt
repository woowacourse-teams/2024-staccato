package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentLocationResponse(
    @SerialName("momentLocationResponses") val momentLocationResponses: List<MomentLocationDto>,
)

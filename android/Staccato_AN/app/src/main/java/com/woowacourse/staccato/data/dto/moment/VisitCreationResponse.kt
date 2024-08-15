package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitCreationResponse(
    @SerialName("visitId") val visitId: Long,
)

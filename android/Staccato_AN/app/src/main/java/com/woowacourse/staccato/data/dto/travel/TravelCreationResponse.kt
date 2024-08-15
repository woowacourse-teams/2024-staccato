package com.woowacourse.staccato.data.dto.travel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelCreationResponse(
    @SerialName("travelId") val travelId: Long,
)

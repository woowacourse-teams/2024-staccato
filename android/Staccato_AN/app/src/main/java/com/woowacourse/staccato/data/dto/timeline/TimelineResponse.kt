package com.woowacourse.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineResponse(
    @SerialName("travels") val travels: List<TimelineMemoryDto>,
)

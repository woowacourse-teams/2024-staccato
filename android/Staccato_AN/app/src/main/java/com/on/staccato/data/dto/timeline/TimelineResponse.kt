package com.on.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineResponse(
    @SerialName("memories") val memories: List<TimelineCategoryDto>,
)

package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.timeline.TimelineMemoryDto
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Travel
import java.time.LocalDate

fun TimelineResponse.toDomain(): Timeline {
    val travels =
        travels.map { timelineTravelDto ->
            timelineTravelDto.toDomain()
        }
    return Timeline(travels)
}

fun TimelineMemoryDto.toDomain(): Travel {
    return Travel(
        travelId = travelId,
        travelThumbnailUrl = travelThumbnailUrl,
        travelTitle = travelTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.map { it.toDomain() },
        visits = emptyList(),
    )
}

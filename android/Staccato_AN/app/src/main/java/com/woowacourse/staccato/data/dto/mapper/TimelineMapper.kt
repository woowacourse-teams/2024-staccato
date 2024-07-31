package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import com.woowacourse.staccato.data.dto.timeline.TimelineTravelDto
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

fun TimelineTravelDto.toDomain(): Travel {
    return Travel(
        travelId = travelId,
        travelThumbnail = travelThumbnail,
        travelTitle = travelTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.toDomain(),
        visits = emptyList(),
    )
}

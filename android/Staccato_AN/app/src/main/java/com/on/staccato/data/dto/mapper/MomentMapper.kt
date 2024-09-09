package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.moment.MomentLocationDto
import com.on.staccato.data.dto.moment.MomentResponse
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Moment
import com.on.staccato.domain.model.MomentLocation
import java.time.LocalDateTime

fun MomentResponse.toDomain() =
    Moment(
        momentId = momentId,
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        placeName = placeName,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = LocalDateTime.parse(visitedAt),
        feeling = Feeling.fromValue(feeling),
        comments = visitLogs.map { it.toDomain() },
    )

fun MomentLocationDto.toDomain() =
    MomentLocation(
        momentId = momentId,
        latitude = latitude,
        longitude = longitude,
    )

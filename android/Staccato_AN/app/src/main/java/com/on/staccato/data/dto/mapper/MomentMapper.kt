package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.staccato.MomentLocationDto
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Moment
import com.on.staccato.domain.model.MomentLocation
import java.time.LocalDate
import java.time.LocalDateTime

fun StaccatoResponse.toDomain() =
    Moment(
        momentId = momentId,
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        staccatoTitle = staccatoTitle,
        placeName = placeName,
        latitude = latitude,
        longitude = longitude,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = LocalDateTime.parse(visitedAt),
        startAt = startAt?.let { LocalDate.parse(startAt) },
        endAt = endAt?.let { LocalDate.parse(endAt) },
        feeling = Feeling.fromValue(feeling),
    )

fun MomentLocationDto.toDomain() =
    MomentLocation(
        momentId = momentId,
        latitude = latitude,
        longitude = longitude,
    )

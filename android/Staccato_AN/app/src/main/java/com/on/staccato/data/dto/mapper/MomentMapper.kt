package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.staccato.StaccatoLocationDto
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.MomentLocation
import com.on.staccato.domain.model.Staccato
import java.time.LocalDate
import java.time.LocalDateTime

fun StaccatoResponse.toDomain() =
    Staccato(
        staccatoId = momentId,
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        staccatoTitle = staccatoTitle,
        placeName = placeName,
        latitude = latitude,
        longitude = longitude,
        staccatoImageUrls = momentImageUrls,
        address = address,
        visitedAt = LocalDateTime.parse(visitedAt),
        startAt = startAt?.let { LocalDate.parse(startAt) },
        endAt = endAt?.let { LocalDate.parse(endAt) },
        feeling = Feeling.fromValue(feeling),
    )

fun StaccatoLocationDto.toDomain() =
    MomentLocation(
        momentId = momentId,
        latitude = latitude,
        longitude = longitude,
    )

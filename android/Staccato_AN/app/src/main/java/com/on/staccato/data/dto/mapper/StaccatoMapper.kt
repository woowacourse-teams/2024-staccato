package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.staccato.StaccatoLocationDto
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoLocation
import java.time.LocalDate
import java.time.LocalDateTime

fun StaccatoResponse.toDomain() =
    Staccato(
        staccatoId = staccatoId,
        memoryId = categoryId,
        memoryTitle = categoryTitle,
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
    StaccatoLocation(
        staccatoId = staccatoId,
        latitude = latitude,
        longitude = longitude,
    )

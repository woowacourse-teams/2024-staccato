package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.staccato.StaccatoMarkerDto
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoShareLinkResponse
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.domain.model.StaccatoShareLink
import java.time.LocalDate
import java.time.LocalDateTime

fun StaccatoResponse.toDomain() =
    Staccato(
        staccatoId = staccatoId,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        staccatoTitle = staccatoTitle,
        placeName = placeName,
        latitude = latitude,
        longitude = longitude,
        staccatoImageUrls = staccatoImageUrls,
        address = address,
        visitedAt = LocalDateTime.parse(visitedAt),
        startAt = startAt?.let { LocalDate.parse(startAt) },
        endAt = endAt?.let { LocalDate.parse(endAt) },
        feeling = Feeling.fromValue(feeling),
    )

fun StaccatoMarkerDto.toDomain() =
    StaccatoMarker(
        staccatoId = staccatoId,
        latitude = latitude,
        longitude = longitude,
        color = color,
        title = title,
        visitedAt = LocalDateTime.parse(visitedAt),
    )

fun StaccatoShareLinkResponse.toDomain() =
    StaccatoShareLink(
        staccatoId = staccatoId,
        shareLink = shareLink,
    )

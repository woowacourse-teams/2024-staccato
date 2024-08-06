package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.travel.TravelRequest
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.data.dto.travel.TravelVisitDto
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.model.NewTravel
import com.woowacourse.staccato.domain.model.TravelVisit
import java.time.LocalDate

fun TravelResponse.toDomain() =
    Travel(
        travelId = travelId,
        travelThumbnail = travelThumbnail,
        travelTitle = travelTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.map { it.toDomain() },
        visits = visits.map { it.toDomain() },
    )

fun TravelVisitDto.toDomain() =
    TravelVisit(
        visitId = visitId,
        placeName = placeName,
        visitImage = visitImage,
        visitedAt = LocalDate.parse(visitedAt),
    )

fun NewTravel.toDto() =
    TravelRequest(
        travelThumbnail = travelThumbnail,
        travelTitle = travelTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )

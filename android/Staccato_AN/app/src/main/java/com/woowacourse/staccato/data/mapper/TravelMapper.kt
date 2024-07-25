package com.woowacourse.staccato.data.mapper

import com.woowacourse.staccato.data.dto.MemberDto
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import com.woowacourse.staccato.data.dto.travel.TravelVisitDto
import com.woowacourse.staccato.domain.model.Member
import com.woowacourse.staccato.domain.model.Travel
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

fun MemberDto.toDomain() =
    Member(
        memberId = memberId,
        nickName = nickName,
        memberImage = memberImage,
    )

fun TravelVisitDto.toDomain() =
    TravelVisit(
        visitId = visitId,
        placeName = placeName,
        visitImage = visitImage,
        visitedAt = LocalDate.parse(visitedAt),
    )

package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Member
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.domain.model.TravelVisit
import com.woowacourse.staccato.presentation.common.MemberUiModel
import com.woowacourse.staccato.presentation.travel.model.TravelUiModel
import com.woowacourse.staccato.presentation.travel.model.TravelVisitUiModel

fun Travel.toUiModel() =
    TravelUiModel(
        id = travelId,
        title = travelTitle,
        thumbnail = travelThumbnail,
        startAt = startAt,
        endAt = endAt,
        description = description,
        mates = mates.map { it.toUiModel() },
        visits = visits.map { it.toUiModel() },
    )

fun Member.toUiModel() =
    MemberUiModel(
        id = memberId,
        nickName = nickName,
        memberImage = memberImage,
    )

fun TravelVisit.toUiModel() =
    TravelVisitUiModel(
        id = visitId,
        placeName = placeName,
        visitImage = visitImage,
        visitedAt = visitedAt,
    )

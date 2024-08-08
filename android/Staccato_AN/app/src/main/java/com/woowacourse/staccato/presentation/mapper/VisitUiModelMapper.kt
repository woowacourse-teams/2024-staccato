package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateUiModel

fun Visit.toVisitDefaultUiModel(): VisitDetailUiModel.VisitDefaultUiModel {
    // TODO: visitImages 빈 배열일 때 처리
    val visitImage = if (visitImageUrls.isEmpty()) "" else visitImageUrls[0]
    return VisitDetailUiModel.VisitDefaultUiModel(
        id = visitId,
        placeName = placeName,
        visitImageUrls = visitImage,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Visit.toVisitUpdateDefaultUiModel() =
    VisitUpdateUiModel(
        placeName = placeName,
        address = address,
    )

fun VisitLog.toVisitLogUiModel() =
    VisitDetailUiModel.VisitLogUiModel(
        id = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

fun Timeline.toTravels(): List<VisitTravelUiModel> =
    this.travels.map { travel ->
        VisitTravelUiModel(
            id = travel.travelId,
            title = travel.travelTitle,
            startAt = travel.startAt,
            endAt = travel.endAt,
        )
    }

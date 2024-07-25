package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateUiModel

fun Visit.toVisitDefaultUiModel(): VisitDetailUiModel.VisitDefaultUiModel {
    // TODO: visitImages 빈 배열일 때 처리
    val visitImage = if (visitImages.isEmpty()) "" else visitImages[0]
    return VisitDetailUiModel.VisitDefaultUiModel(
        id = visitId,
        placeName = placeName,
        visitImage = visitImage,
        address = address,
        visitedAt = visitedAt,
        visitedCount = visitedCount,
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
        nickName = nickName,
        memberImage = memberImage,
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

package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel
import java.time.LocalDateTime
import java.time.LocalTime

fun Visit.toVisitDefaultUiModel(): VisitDetailUiModel.VisitDefaultUiModel {
    return VisitDetailUiModel.VisitDefaultUiModel(
        id = visitId,
        placeName = placeName,
        visitImageUrls = visitImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Visit.toVisitUpdateDefaultUiModel(): VisitUpdateDefaultUiModel {
    return VisitUpdateDefaultUiModel(
        id = visitId,
        address = address,
        visitedAt = LocalDateTime.of(visitedAt, LocalTime.of(0, 0, 0)),
    )
}

fun VisitLog.toVisitLogUiModel() =
    VisitDetailUiModel.VisitLogUiModel(
        id = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

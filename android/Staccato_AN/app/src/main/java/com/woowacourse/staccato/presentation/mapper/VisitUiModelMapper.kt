package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.moment.model.VisitDetailUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel
import java.time.LocalDateTime
import java.time.LocalTime

fun Moment.toVisitDefaultUiModel(): VisitDetailUiModel.VisitDefaultUiModel {
    return VisitDetailUiModel.VisitDefaultUiModel(
        id = momentId,
        placeName = placeName,
        visitImageUrls = momentImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Moment.toVisitUpdateDefaultUiModel(): VisitUpdateDefaultUiModel {
    return VisitUpdateDefaultUiModel(
        id = momentId,
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

package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.moment.model.MomentDetailUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateDefaultUiModel

fun Moment.toVisitDefaultUiModel(): MomentDetailUiModel.MomentDefaultUiModel {
    return MomentDetailUiModel.MomentDefaultUiModel(
        id = momentId,
        placeName = placeName,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = visitedAt,
    )
}

fun Moment.toVisitUpdateDefaultUiModel(): VisitUpdateDefaultUiModel {
    return VisitUpdateDefaultUiModel(
        id = momentId,
        address = address,
        visitedAt = visitedAt,
    )
}

fun VisitLog.toVisitLogUiModel() =
    MomentDetailUiModel.CommentsUiModel(
        id = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

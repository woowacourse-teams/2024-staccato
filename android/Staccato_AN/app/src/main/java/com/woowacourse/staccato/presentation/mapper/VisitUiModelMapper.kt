package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel
import com.woowacourse.staccato.presentation.visitupdate.model.VisitUpdateUiModel

fun Visit.toVisitDefaultUiModel() =
    VisitDetailUiModel.VisitDefaultUiModel(
        id = visitId,
        placeName = placeName,
        visitImages = visitImages[0],
        address = address,
        visitedAt = visitedAt,
        visitedCount = visitedCount,
    )

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

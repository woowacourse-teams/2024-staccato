package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.visit.VisitLogDto
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import java.time.LocalDate

fun VisitResponse.toDomain() =
    Visit(
        visitId = visitId,
        placeName = placeName,
        visitImages = visitImages,
        address = address,
        visitedAt = LocalDate.parse(visitedAt),
        visitedCount = visitedCount,
        visitLogs = visitLogs.map { it.toDomain() },
    )

fun VisitLogDto.toDomain() =
    VisitLog(
        visitLogId = visitLogId,
        memberId = memberId,
        nickName = nickName,
        memberImage = memberImage,
        content = content,
    )

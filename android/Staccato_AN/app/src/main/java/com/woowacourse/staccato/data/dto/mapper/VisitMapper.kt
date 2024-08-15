package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.moment.VisitLogDto
import com.woowacourse.staccato.data.dto.moment.VisitResponse
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.model.VisitLog
import java.time.LocalDate

fun VisitResponse.toDomain() =
    Visit(
        visitId = visitId,
        placeName = placeName,
        visitImageUrls = visitImageUrls,
        address = address,
        visitedAt = LocalDate.parse(visitedAt),
        visitLogs = visitLogs.map { it.toDomain() },
    )

fun VisitLogDto.toDomain() =
    VisitLog(
        visitLogId = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

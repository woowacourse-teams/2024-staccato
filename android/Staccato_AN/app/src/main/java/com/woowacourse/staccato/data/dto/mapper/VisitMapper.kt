package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.moment.VisitLogDto
import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.VisitLog
import java.time.LocalDate

fun MomentResponse.toDomain() =
    Moment(
        momentId = momentId,
        placeName = placeName,
        momentImageUrls = momentImageUrls,
        address = address,
        visitedAt = LocalDate.parse(visitedAt),
        comments = visitLogs.map { it.toDomain() },
    )

fun VisitLogDto.toDomain() =
    VisitLog(
        visitLogId = visitLogId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

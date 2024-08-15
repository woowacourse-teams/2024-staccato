package com.woowacourse.staccato.presentation.mapper

import com.woowacourse.staccato.domain.model.Member
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.MemoryVisit
import com.woowacourse.staccato.presentation.common.MemberUiModel
import com.woowacourse.staccato.presentation.memory.model.MemoryUiModel
import com.woowacourse.staccato.presentation.memory.model.MemoryVisitUiModel

fun Memory.toUiModel() =
    MemoryUiModel(
        id = travelId,
        title = travelTitle,
        travelThumbnailUrl = travelThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
        description = description,
        mates = mates.map { it.toUiModel() },
        visits = visits.map { it.toUiModel() },
    )

fun Member.toUiModel() =
    MemberUiModel(
        id = memberId,
        nickname = nickname,
        memberImage = memberImage,
    )

fun MemoryVisit.toUiModel() =
    MemoryVisitUiModel(
        id = visitId,
        placeName = placeName,
        visitImageUrl = visitImageUrl,
        visitedAt = visitedAt,
    )

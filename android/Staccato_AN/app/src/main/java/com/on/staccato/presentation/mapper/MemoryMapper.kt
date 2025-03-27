package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryStaccato
import com.on.staccato.presentation.common.MemberUiModel
import com.on.staccato.presentation.memory.model.MemoryStaccatoUiModel
import com.on.staccato.presentation.memory.model.MemoryUiModel

fun Memory.toUiModel() =
    MemoryUiModel(
        id = memoryId,
        title = memoryTitle,
        memoryThumbnailUrl = memoryThumbnailUrl,
        startAt = startAt,
        endAt = endAt,
        description = description,
        mates = mates.map { it.toUiModel() },
        staccatos = staccatos.map { it.toUiModel() },
    )

fun Member.toUiModel() =
    MemberUiModel(
        id = memberId,
        nickname = nickname,
        memberImage = memberImage,
    )

fun MemoryStaccato.toUiModel() =
    MemoryStaccatoUiModel(
        id = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = visitedAt,
    )

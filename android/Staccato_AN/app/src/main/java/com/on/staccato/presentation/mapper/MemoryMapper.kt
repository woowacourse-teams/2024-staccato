package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryStaccato
import com.on.staccato.presentation.common.MemberUiModel
import com.on.staccato.presentation.memory.model.MemoryStaccatoUiModel
import com.on.staccato.presentation.memory.model.MemoryUiModel

fun Category.toUiModel() =
    MemoryUiModel(
        id = categoryId,
        title = categoryTitle,
        memoryThumbnailUrl = categoryThumbnailUrl,
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

fun CategoryStaccato.toUiModel() =
    MemoryStaccatoUiModel(
        id = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = visitedAt,
    )

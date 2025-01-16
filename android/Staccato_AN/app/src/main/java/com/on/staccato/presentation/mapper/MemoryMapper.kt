package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryStaccato
import com.on.staccato.presentation.common.MemberUiModel
import com.on.staccato.presentation.category.model.CategoryStaccatoUiModel
import com.on.staccato.presentation.category.model.CategoryUiModel

fun Category.toUiModel() =
    CategoryUiModel(
        id = categoryId,
        title = categoryTitle,
        categoryThumbnailUrl = categoryThumbnailUrl,
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
    CategoryStaccatoUiModel(
        id = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = visitedAt,
    )

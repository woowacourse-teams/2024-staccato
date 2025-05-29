package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryStaccato
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
        color = color,
        members = participants.map { it.member },
        staccatos = staccatos.map { it.toUiModel() },
        isShared = isShared,
        myRole = myRole,
    )

fun CategoryStaccato.toUiModel() =
    CategoryStaccatoUiModel(
        id = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = visitedAt,
    )

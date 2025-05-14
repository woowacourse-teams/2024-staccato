package com.on.staccato.presentation.category

import com.on.staccato.domain.model.Category
import com.on.staccato.presentation.category.model.CategoryUiModel
import com.on.staccato.presentation.common.color.CategoryColor

const val VALID_ID = 1L
const val INVALID_ID = 0L

val category =
    Category(
        categoryId = 1,
        categoryThumbnailUrl = null,
        categoryTitle = "카테고리 제목",
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
        mates = listOf(),
        staccatos = listOf(),
    )

val categoryUiModel =
    CategoryUiModel(
        id = 1,
        title = "카테고리 제목",
        categoryThumbnailUrl = null,
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
        members = listOf(),
        staccatos = listOf(),
    )

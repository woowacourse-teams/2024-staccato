package com.on.staccato.presentation.categorycreation

import com.on.staccato.domain.model.NewCategory
import com.on.staccato.presentation.common.color.CategoryColor

val newCategory =
    NewCategory(
        categoryThumbnailUrl = null,
        categoryTitle = "",
        description = null,
        color = CategoryColor.GRAY.label,
        startAt = null,
        endAt = null,
        isShared = false,
    )

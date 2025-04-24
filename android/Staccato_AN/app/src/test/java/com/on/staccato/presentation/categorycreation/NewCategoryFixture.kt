package com.on.staccato.presentation.categorycreation

import com.on.staccato.domain.model.NewCategory
import com.on.staccato.presentation.common.color.CategoryColor

val newCategory =
    NewCategory(
        categoryThumbnailUrl = null,
        categoryTitle = "",
        startAt = null,
        endAt = null,
        description = null,
        color = CategoryColor.GRAY.label,
    )

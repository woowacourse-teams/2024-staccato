package com.on.staccato.domain.model

import java.time.LocalDate

data class NewCategory(
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val description: String? = null,
    val color: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val isShared: Boolean = false,
)

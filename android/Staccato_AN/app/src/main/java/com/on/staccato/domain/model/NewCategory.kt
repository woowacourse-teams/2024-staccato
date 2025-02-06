package com.on.staccato.domain.model

import java.time.LocalDate

data class NewCategory(
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
)

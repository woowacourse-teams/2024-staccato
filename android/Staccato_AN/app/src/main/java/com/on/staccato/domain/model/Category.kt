package com.on.staccato.domain.model

import java.time.LocalDate

data class Category(
    val categoryId: Long,
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
    val color: String,
    val mates: List<Participant>,
    val staccatos: List<CategoryStaccato>,
    val isShared: Boolean,
)

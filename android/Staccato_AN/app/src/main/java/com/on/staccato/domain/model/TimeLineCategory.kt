package com.on.staccato.domain.model

import java.time.LocalDate

data class TimeLineCategory(
    val categoryId: Long,
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val color: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val mates: List<Member>,
    val staccatos: List<CategoryStaccato>,
)

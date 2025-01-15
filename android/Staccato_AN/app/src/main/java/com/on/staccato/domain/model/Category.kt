package com.on.staccato.domain.model

import java.time.LocalDate

data class Category(
    val categoryId: Long,
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
    val mates: List<Member>,
    val staccatos: List<MemoryStaccato>,
)

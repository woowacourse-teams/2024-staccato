package com.on.staccato.domain.model

import java.time.LocalDate

data class TimeLineCategory(
    val categoryId: Long,
    val categoryThumbnailUrl: String? = null,
    val categoryTitle: String,
    val color: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val isShared: Boolean,
    val participants: List<Member>,
    val staccatoCount: Int,
)

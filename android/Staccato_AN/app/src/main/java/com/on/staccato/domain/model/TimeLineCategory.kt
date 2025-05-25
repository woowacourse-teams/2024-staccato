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
    val totalMemberCount: Long,
    val members: List<Member>,
    val staccatoCount: Long,
)

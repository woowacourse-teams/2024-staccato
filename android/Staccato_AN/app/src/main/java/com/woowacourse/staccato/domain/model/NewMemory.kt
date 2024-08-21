package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class NewMemory(
    val memoryThumbnailUrl: String? = null,
    val memoryTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
)

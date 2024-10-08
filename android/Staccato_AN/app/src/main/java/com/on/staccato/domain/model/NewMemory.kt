package com.on.staccato.domain.model

import java.time.LocalDate

data class NewMemory(
    val memoryThumbnailUrl: String? = null,
    val memoryTitle: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
)

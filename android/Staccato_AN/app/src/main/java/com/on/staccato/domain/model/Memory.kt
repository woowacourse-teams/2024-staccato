package com.on.staccato.domain.model

import java.time.LocalDate

data class Memory(
    val memoryId: Long,
    val memoryThumbnailUrl: String? = null,
    val memoryTitle: String,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
    val mates: List<Member>,
    val staccatos: List<MemoryStaccato>,
)

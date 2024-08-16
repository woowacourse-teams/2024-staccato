package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Memory(
    val memoryId: Long,
    val memoryThumbnailUrl: String? = null,
    val memoryTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
    val mates: List<Member>,
    val moments: List<MemoryVisit>,
)

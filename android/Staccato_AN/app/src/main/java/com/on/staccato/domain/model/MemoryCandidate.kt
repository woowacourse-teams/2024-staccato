package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>)

data class MemoryCandidate(
    val memoryId: Long,
    val memoryTitle: String,
    val startAt: LocalDate?,
    val endAt: LocalDate?,
)

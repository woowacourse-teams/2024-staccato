package com.on.staccato.domain.model

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>)

data class MemoryCandidate(
    val memoryId: Long,
    val memoryTitle: String,
)

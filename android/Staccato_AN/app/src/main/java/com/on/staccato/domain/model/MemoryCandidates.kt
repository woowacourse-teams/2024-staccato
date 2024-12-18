package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>) {
    fun filterCandidatesBy(date: LocalDate): List<MemoryCandidate> = memoryCandidate.filter { it.isDateWithinPeriod(date) }

    fun findCandidatesBy(memoryId: Long): MemoryCandidate? = memoryCandidate.find { it.memoryId == memoryId }
}

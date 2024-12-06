package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>) {
    fun filterCandidatesBy(date: LocalDate): List<MemoryCandidate> = memoryCandidate.filter { it.isDateWithinPeriod(date) }

    fun filterCandidatesBy(memoryId: Long): List<MemoryCandidate> = memoryCandidate.filter { it.memoryId == memoryId }
}

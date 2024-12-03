package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>) {
    fun getValidMemoryCandidate(date: LocalDate) = memoryCandidate.filter { it.isDateWithinPeriod(date) }
}

package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val memoryCandidate: List<MemoryCandidate>) {
    fun filterBy(date: LocalDate): MemoryCandidates = copy(memoryCandidate = memoryCandidate.filter { it.isDateWithinPeriod(date) })

    fun findBy(memoryId: Long): MemoryCandidate? = memoryCandidate.find { it.categoryId == memoryId }

    fun findByIdOrFirst(targetId: Long?): MemoryCandidate? =
        memoryCandidate.find { it.categoryId == targetId }
            ?: memoryCandidate.firstOrNull()

    companion object {
        val emptyMemoryCandidates = MemoryCandidates(emptyList())

        fun from(vararg memoryCandidate: MemoryCandidate) = MemoryCandidates(listOf(*memoryCandidate))
    }
}

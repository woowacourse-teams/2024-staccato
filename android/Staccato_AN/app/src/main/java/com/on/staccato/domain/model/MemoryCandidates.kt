package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val categoryCandidates: List<CategoryCandidate>) {
    fun filterBy(date: LocalDate): MemoryCandidates = copy(categoryCandidates = categoryCandidates.filter { it.isDateWithinPeriod(date) })

    fun findBy(memoryId: Long): CategoryCandidate? = categoryCandidates.find { it.categoryId == memoryId }

    fun findByIdOrFirst(targetId: Long?): CategoryCandidate? =
        categoryCandidates.find { it.categoryId == targetId }
            ?: categoryCandidates.firstOrNull()

    companion object {
        val emptyMemoryCandidates = MemoryCandidates(emptyList())

        fun from(vararg memoryCandidate: CategoryCandidate) = MemoryCandidates(listOf(*memoryCandidate))
    }
}

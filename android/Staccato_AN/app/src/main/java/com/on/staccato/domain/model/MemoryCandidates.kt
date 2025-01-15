package com.on.staccato.domain.model

import java.time.LocalDate

data class MemoryCandidates(val categoryCandidates: List<CategoryCandidate>) {
    fun filterBy(date: LocalDate): MemoryCandidates = copy(categoryCandidates = categoryCandidates.filter { it.isDateWithinPeriod(date) })

    fun findBy(categoryId: Long): CategoryCandidate? = categoryCandidates.find { it.categoryId == categoryId }

    fun findByIdOrFirst(targetId: Long?): CategoryCandidate? =
        categoryCandidates.find { it.categoryId == targetId }
            ?: categoryCandidates.firstOrNull()

    companion object {
        val emptyCategoryCandidates = MemoryCandidates(emptyList())

        fun from(vararg categoryCandidate: CategoryCandidate) = MemoryCandidates(listOf(*categoryCandidate))
    }
}

package com.on.staccato.domain.model

import java.time.LocalDate

data class CategoryCandidates(val categoryCandidates: List<CategoryCandidate>) {
    fun filterBy(date: LocalDate): CategoryCandidates = copy(categoryCandidates = categoryCandidates.filter { it.isDateWithinPeriod(date) })

    fun findBy(categoryId: Long): CategoryCandidate? = categoryCandidates.find { it.categoryId == categoryId }

    fun findByIdOrFirst(targetId: Long?): CategoryCandidate? =
        categoryCandidates.find { it.categoryId == targetId }
            ?: categoryCandidates.firstOrNull()

    companion object {
        val emptyCategoryCandidates = CategoryCandidates(emptyList())

        fun from(vararg categoryCandidate: CategoryCandidate) = CategoryCandidates(listOf(*categoryCandidate))
    }
}

package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.timeline.TimelineCategoryDto
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.Timeline
import java.time.LocalDate

fun TimelineResponse.toDomain(): Timeline {
    val categories =
        categories.map { timelineCategoryDto ->
            timelineCategoryDto.toDomain()
        }
    return Timeline(categories)
}

fun TimelineResponse.toCategoryCandidates(): CategoryCandidates {
    val memories: List<CategoryCandidate> =
        categories.map { timelineCategoryDto ->
            timelineCategoryDto.toCategoryCandidate()
        }
    return CategoryCandidates(memories)
}

fun TimelineCategoryDto.toDomain(): Category {
    return Category(
        categoryId = categoryId,
        categoryThumbnailUrl = categoryThumbnailUrl,
        categoryTitle = categoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
        description = description,
        mates = emptyList(),
        staccatos = emptyList(),
    )
}

fun TimelineCategoryDto.toCategoryCandidate(): CategoryCandidate {
    return CategoryCandidate(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
    )
}

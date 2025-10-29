package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.timeline.TimelineCategoryDto
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.TimeLineCategory
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
    val categories: List<CategoryCandidate> =
        categories.map { timelineCategoryDto ->
            timelineCategoryDto.toCategoryCandidate()
        }
    return CategoryCandidates(categories)
}

fun TimelineCategoryDto.toDomain(): TimeLineCategory =
    TimeLineCategory(
        categoryId = categoryId,
        categoryThumbnailUrl = categoryThumbnailUrl,
        categoryTitle = categoryTitle,
        color = color,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
        isShared = isShared,
        totalMemberCount = totalMemberCount,
        members = members.map { it.toDomain() },
        staccatoCount = staccatoCount,
    )

fun TimelineCategoryDto.toCategoryCandidate(): CategoryCandidate =
    CategoryCandidate(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
    )

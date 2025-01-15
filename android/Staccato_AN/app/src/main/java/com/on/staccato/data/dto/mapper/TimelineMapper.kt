package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.timeline.TimelineMemoryDto
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.model.Category
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.Timeline
import java.time.LocalDate

fun TimelineResponse.toDomain(): Timeline {
    val memories =
        memories.map { timelineMemoryDto ->
            timelineMemoryDto.toDomain()
        }
    return Timeline(memories)
}

fun TimelineResponse.toMemoryCandidates(): MemoryCandidates {
    val memories: List<CategoryCandidate> =
        memories.map { timelineMemoryDto ->
            timelineMemoryDto.toMemoryCandidate()
        }
    return MemoryCandidates(memories)
}

fun TimelineMemoryDto.toDomain(): Category {
    return Category(
        categoryId = memoryId,
        categoryThumbnailUrl = memoryThumbnailUrl,
        categoryTitle = memoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
        description = description,
        mates = emptyList(),
        staccatos = emptyList(),
    )
}

fun TimelineMemoryDto.toMemoryCandidate(): CategoryCandidate {
    return CategoryCandidate(
        categoryId = memoryId,
        categoryTitle = memoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
    )
}

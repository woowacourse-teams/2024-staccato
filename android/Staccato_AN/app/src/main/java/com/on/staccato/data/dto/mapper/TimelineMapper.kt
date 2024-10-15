package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.timeline.TimelineMemoryDto
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidate
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
    val memories: List<MemoryCandidate> =
        memories.map { timelineMemoryDto ->
            timelineMemoryDto.toMemoryCandidate()
        }
    return MemoryCandidates(memories)
}

fun TimelineMemoryDto.toDomain(): Memory {
    return Memory(
        memoryId = memoryId,
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
        description = description,
        mates = emptyList(),
        staccatos = emptyList(),
    )
}

fun TimelineMemoryDto.toMemoryCandidate(): MemoryCandidate {
    return MemoryCandidate(
        memoryId = memoryId,
        memoryTitle = memoryTitle,
        startAt = startAt?.let { LocalDate.parse(it) },
        endAt = endAt?.let { LocalDate.parse(it) },
    )
}

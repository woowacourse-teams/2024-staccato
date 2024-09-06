package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.timeline.TimelineMemoryDto
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.Timeline
import java.time.LocalDate

fun TimelineResponse.toDomain(): Timeline {
    val memories =
        memories.map { timelineMemoryDto ->
            timelineMemoryDto.toDomain()
        }
    return Timeline(memories)
}

fun TimelineMemoryDto.toDomain(): Memory {
    return Memory(
        memoryId = memoryId,
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = emptyList(),
        moments = emptyList(),
    )
}

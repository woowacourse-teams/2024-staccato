package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import com.woowacourse.staccato.data.dto.timeline.TimelineMemoryDto
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Memory
import java.time.LocalDate

fun TimelineResponse.toDomain(): Timeline {
    val Memories =
        Memories.map { timelineMemoryDto ->
            timelineMemoryDto.toDomain()
        }
    return Timeline(Memories)
}

fun TimelineMemoryDto.toDomain(): Memory {
    return Memory(
        memoryId = MemoryId,
        memoryThumbnailUrl = MemoryThumbnailUrl,
        memoryTitle = MemoryTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.map { it.toDomain() },
        visits = emptyList(),
    )
}

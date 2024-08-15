package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.timeline.TimelineMemoryDto
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.Timeline
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
        mates = mates.map { it.toDomain() },
        visits = emptyList(),
    )
}

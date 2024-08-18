package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.memory.MemoryMomentDto
import com.woowacourse.staccato.data.dto.memory.MemoryRequest
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.data.dto.memory.MemoryUpdateRequest
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.MemoryMoment
import com.woowacourse.staccato.domain.model.NewMemory
import java.time.LocalDate

fun MemoryResponse.toDomain() =
    Memory(
        memoryId = memoryId,
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.map { it.toDomain() },
        moments = moments.map { it.toDomain() },
    )

fun MemoryMomentDto.toDomain() =
    MemoryMoment(
        momentId = momentId,
        placeName = placeName,
        momentImageUrl = momentImageUrl,
        visitedAt = LocalDate.parse(visitedAt),
    )

fun NewMemory.toDto() =
    MemoryRequest(
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )

fun NewMemory.toMemoryUpdateRequest() =
    MemoryUpdateRequest(
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )

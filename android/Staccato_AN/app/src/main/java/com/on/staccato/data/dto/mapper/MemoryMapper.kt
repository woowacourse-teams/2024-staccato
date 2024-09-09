package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryMomentDto
import com.on.staccato.data.dto.memory.MemoryRequest
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.MemoryMoment
import com.on.staccato.domain.model.NewMemory
import java.time.LocalDate
import java.time.LocalDateTime

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

fun MemoriesResponse.toDomain(): MemoryCandidates =
    MemoryCandidates(
        this.memories.map {
            MemoryCandidate(
                memoryId = it.memoryId,
                memoryTitle = it.memoryTitle,
            )
        },
    )

fun MemoryMomentDto.toDomain() =
    MemoryMoment(
        momentId = momentId,
        placeName = placeName,
        momentImageUrl = momentImageUrl,
        visitedAt = LocalDateTime.parse(visitedAt),
    )

fun NewMemory.toDto() =
    MemoryRequest(
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )
package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryRequest
import com.on.staccato.data.dto.memory.MemoryResponse
import com.on.staccato.data.dto.memory.MemoryStaccatoDto
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.MemoryStaccato
import com.on.staccato.domain.model.NewMemory
import java.time.LocalDate
import java.time.LocalDateTime

fun MemoryResponse.toDomain() =
    Memory(
        memoryId = memoryId,
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        startAt = startAt?.let { LocalDate.parse(startAt) },
        endAt = endAt?.let { LocalDate.parse(endAt) },
        description = description,
        mates = mates.map { it.toDomain() },
        staccatos = staccatos.map { it.toDomain() },
    )

fun MemoriesResponse.toDomain(): MemoryCandidates =
    MemoryCandidates(
        this.categories.map {
            MemoryCandidate(
                memoryId = it.categoryId,
                memoryTitle = it.categoryTitle,
                startAt = LocalDate.parse(it.startAt),
                endAt = LocalDate.parse(it.endAt),
            )
        },
    )

fun MemoryStaccatoDto.toDomain() =
    MemoryStaccato(
        staccatoId = staccatoId,
        staccatoTitle = staccatoTitle,
        staccatoImageUrl = staccatoImageUrl,
        visitedAt = LocalDateTime.parse(visitedAt),
    )

fun NewMemory.toDto() =
    MemoryRequest(
        memoryThumbnailUrl = memoryThumbnailUrl,
        memoryTitle = memoryTitle,
        description = description,
        startAt = startAt?.toString(),
        endAt = endAt?.toString(),
    )

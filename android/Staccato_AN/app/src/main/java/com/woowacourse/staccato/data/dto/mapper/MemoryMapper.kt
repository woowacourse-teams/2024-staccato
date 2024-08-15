package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.memory.MemoryRequest
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.data.dto.memory.MemoryUpdateRequest
import com.woowacourse.staccato.data.dto.memory.MemoryVisitDto
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.MemoryVisit
import com.woowacourse.staccato.domain.model.NewMemory
import java.time.LocalDate

fun MemoryResponse.toDomain() =
    Memory(
        travelId = memoryId,
        travelThumbnailUrl = memoryThumbnailUrl,
        travelTitle = memoryTitle,
        startAt = LocalDate.parse(startAt),
        endAt = LocalDate.parse(endAt),
        description = description,
        mates = mates.map { it.toDomain() },
        visits = visits.map { it.toDomain() },
    )

fun MemoryVisitDto.toDomain() =
    MemoryVisit(
        visitId = visitId,
        placeName = placeName,
        visitImageUrl = visitImageUrl,
        visitedAt = LocalDate.parse(visitedAt),
    )

fun NewMemory.toDto() =
    MemoryRequest(
        memoryTitle = travelTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )

fun NewMemory.toTravelUpdateRequest() =
    MemoryUpdateRequest(
        memoryThumbnailUrl = travelThumbnail,
        memoryTitle = travelTitle,
        description = description,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
    )

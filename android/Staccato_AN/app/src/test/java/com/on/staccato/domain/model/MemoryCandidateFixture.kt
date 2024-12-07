package com.on.staccato.domain.model

import java.time.LocalDate

internal val yearEnd2023 = LocalDate.of(2023, 12, 31)
internal val yearStart2024 = LocalDate.of(2024, 1, 1)
internal val yearMiddle2024 = LocalDate.of(2024, 7, 1)
internal val yearEnd2024 = LocalDate.of(2024, 12, 31)
internal val yearStart2025 = LocalDate.of(2025, 1, 1)

internal const val TARGET_MEMORY_ID = 4L

internal val newMemoryCandidate =
    makeTestMemoryCandidate(
        memoryId = 1L,
        startAt = yearEnd2023,
        endAt = yearStart2024,
    )

internal val targetMemoryCandidate =
    makeTestMemoryCandidate(
        memoryId = TARGET_MEMORY_ID,
        startAt = yearEnd2024,
        endAt = yearStart2025,
    )

val dummyMemoryCandidates =
    MemoryCandidates(
        memoryCandidate =
            listOf(
                newMemoryCandidate,
                makeTestMemoryCandidate(memoryId = 2L, startAt = yearStart2024, endAt = yearMiddle2024),
                makeTestMemoryCandidate(memoryId = 3L, startAt = yearMiddle2024, endAt = yearEnd2024),
                targetMemoryCandidate,
            ),
    )

internal const val TARGET_STACCATO_ID = 4L

internal val targetStaccato =
    Staccato(
        staccatoId = TARGET_STACCATO_ID,
        staccatoTitle = "테스트용 스타카토",
        placeName = "Elmer Moon",
        address = "nunc",
        latitude = 8.9,
        longitude = 10.11,
        staccatoImageUrls = listOf(),
        memoryId = targetMemoryCandidate.memoryId,
        memoryTitle = targetMemoryCandidate.memoryTitle,
        visitedAt = yearEnd2024.atTime(12, 0),
        startAt = targetMemoryCandidate.startAt,
        endAt = targetMemoryCandidate.endAt,
        feeling = Feeling.EXCITED,
    )

internal fun makeTestMemoryCandidate(
    memoryId: Long = 1L,
    memoryTitle: String = "임시 카테고리",
    startAt: LocalDate? = null,
    endAt: LocalDate? = null,
) = MemoryCandidate(
    memoryId = memoryId,
    memoryTitle = memoryTitle,
    startAt = startAt,
    endAt = endAt,
)

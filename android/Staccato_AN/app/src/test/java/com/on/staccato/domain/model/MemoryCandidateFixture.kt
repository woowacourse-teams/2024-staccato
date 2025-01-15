package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

internal val endDateOf2023 = LocalDate.of(2023, 12, 31)
internal val startDateOf2024 = LocalDate.of(2024, 1, 1)
internal val middleDateOf2024 = LocalDate.of(2024, 7, 1)
internal val endDateOf2024 = LocalDate.of(2024, 12, 31)
internal val startDateOf2025 = LocalDate.of(2025, 1, 1)

internal const val TARGET_MEMORY_ID = 4L

internal val memoryCandidateWithId1 =
    makeTestMemoryCandidate(
        memoryId = 1L,
        startAt = endDateOf2023,
        endAt = startDateOf2024,
    )

internal val targetMemoryCandidate =
    makeTestMemoryCandidate(
        memoryId = TARGET_MEMORY_ID,
        startAt = endDateOf2024,
        endAt = startDateOf2025,
    )

val dummyMemoryCandidates =
    MemoryCandidates(
        memoryCandidate =
            listOf(
                memoryCandidateWithId1,
                makeTestMemoryCandidate(memoryId = 2L, startAt = startDateOf2024, endAt = middleDateOf2024),
                makeTestMemoryCandidate(memoryId = 3L, startAt = middleDateOf2024, endAt = endDateOf2024),
                targetMemoryCandidate,
            ),
    )

internal const val TARGET_STACCATO_ID = 4L

internal fun makeTestMemoryCandidate(
    memoryId: Long = 1L,
    memoryTitle: String = "임시 카테고리",
    startAt: LocalDate? = null,
    endAt: LocalDate? = null,
) = CategoryCandidate(
    categoryId = memoryId,
    categoryTitle = memoryTitle,
    startAt = startAt,
    endAt = endAt,
)

internal fun makeTestStaccato(
    staccatoId: Long = 1L,
    staccatoTitle: String = "test",
    placeName: String = "test",
    address: String = "test",
    latitude: Double = 1.1,
    longitude: Double = 1.1,
    staccatoImageUrls: List<String> = emptyList(),
    visitedAt: LocalDateTime,
    memoryCandidate: CategoryCandidate,
    feeling: Feeling = Feeling.EXCITED,
) = Staccato(
    staccatoId = staccatoId,
    staccatoTitle = staccatoTitle,
    placeName = placeName,
    address = address,
    latitude = latitude,
    longitude = longitude,
    staccatoImageUrls = staccatoImageUrls,
    memoryId = memoryCandidate.categoryId,
    memoryTitle = memoryCandidate.categoryTitle,
    visitedAt = visitedAt,
    startAt = memoryCandidate.startAt,
    endAt = memoryCandidate.endAt,
    feeling = feeling,
)

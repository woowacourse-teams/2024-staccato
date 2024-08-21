package com.woowacourse.staccato.presentation.memory.model

import java.time.LocalDateTime

data class MemoryVisitUiModel(
    val id: Long,
    val placeName: String,
    val visitImageUrl: String? = null,
    val visitedAt: LocalDateTime,
)

val dummyVisits: List<MemoryVisitUiModel> =
    listOf(
        MemoryVisitUiModel(
            id = 1L,
            placeName = "곽지 해수욕장",
            visitImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvaUxFadnGqvUfuzK67Bf-R-2UdJAs_EJH2A&s",
            visitedAt = LocalDateTime.of(2024, 6, 15, 11, 11, 11),
        ),
        MemoryVisitUiModel(
            id = 2L,
            placeName = "섭지 코지",
            visitImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS67vss2wQ541Dj1PqyQjjJHdOYPa4cGtqyGg&s",
            visitedAt = LocalDateTime.of(2024, 6, 15, 11, 11, 11),
        ),
        MemoryVisitUiModel(
            id = 3L,
            placeName = "금오름",
            visitImageUrl = "https://www.ktsketch.co.kr/news/photo/202309/7714_39717_1352.jpg",
            visitedAt = LocalDateTime.of(2024, 6, 17, 11, 11, 11),
        ),
    )

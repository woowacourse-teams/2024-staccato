package com.woowacourse.staccato.presentation.travel.model

import java.time.LocalDate

data class TravelVisitUiModel(
    val id: Long,
    val placeName: String,
    val visitImage: String,
    val visitedAt: LocalDate,
)

 val dummyVisits: List<TravelVisitUiModel> =
    listOf(
        TravelVisitUiModel(
            id = 1L,
            placeName = "곽지 해수욕장",
            visitImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvaUxFadnGqvUfuzK67Bf-R-2UdJAs_EJH2A&s",
            visitedAt = LocalDate.of(2024, 6, 15),
        ),
        TravelVisitUiModel(
            id = 2L,
            placeName = "섭지 코지",
            visitImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS67vss2wQ541Dj1PqyQjjJHdOYPa4cGtqyGg&s",
            visitedAt = LocalDate.of(2024, 6, 15),
        ),
        TravelVisitUiModel(
            id = 3L,
            placeName = "금오름",
            visitImage = "https://www.ktsketch.co.kr/news/photo/202309/7714_39717_1352.jpg",
            visitedAt = LocalDate.of(2024, 6, 17),
        ),
    )

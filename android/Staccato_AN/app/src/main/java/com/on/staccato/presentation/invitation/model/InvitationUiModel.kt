package com.on.staccato.presentation.invitation.model

import java.time.LocalDate

data class InvitationUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val startAt: LocalDate?,
    val endAt: LocalDate?,
    val inviterNickname: String,
    val inviterProfileImageUrl: String? = null,
)

val dummyInvitationUiModels = listOf(
    InvitationUiModel(
        categoryId = 0L,
        categoryTitle = "여름 부산 여행",
        startAt = LocalDate.of(2025, 8, 17),
        endAt = LocalDate.of(2025, 8, 18),
        inviterNickname = "호두",
    ),
    InvitationUiModel(
        categoryId = 1L,
        categoryTitle = "서울 존맛탱구리집 탐방",
        startAt = null,
        endAt = null,
        inviterNickname = "해나",
    ),
    InvitationUiModel(
        categoryId = 2L,
        categoryTitle = "클라이밍하러 성수 꼬고~~~~~~",
        startAt = LocalDate.of(2024, 8, 17),
        endAt = LocalDate.of(2024, 8, 17),
        inviterNickname = "리니",
    ),
    InvitationUiModel(
        categoryId = 3L,
        categoryTitle = "빙빙의 예카 모음집",
        startAt = null,
        endAt = null,
        inviterNickname = "빙티",
    ),
)

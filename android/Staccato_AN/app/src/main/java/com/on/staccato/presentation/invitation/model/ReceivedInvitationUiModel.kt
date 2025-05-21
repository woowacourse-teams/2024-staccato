package com.on.staccato.presentation.invitation.model

data class ReceivedInvitationUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val inviterId: Long,
    val inviterNickname: String,
    val inviterProfileImageUrl: String? = null,
)

val dummyReceivedInvitationUiModels = listOf(
    ReceivedInvitationUiModel(
        categoryId = 0L,
        categoryTitle = "여름 부산 여행",
        inviterId = 0L,
        inviterNickname = "호두",
    ),
    ReceivedInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "서울 존맛탱구리집 탐방",
        inviterId = 1L,
        inviterNickname = "해나",
    ),
    ReceivedInvitationUiModel(
        categoryId = 2L,
        categoryTitle = "클라이밍하러 성수 꼬고~~~~~~",
        inviterId = 2L,
        inviterNickname = "리니",
    ),
    ReceivedInvitationUiModel(
        categoryId = 3L,
        categoryTitle = "빙빙의 예카 모음집",
        inviterId = 3L,
        inviterNickname = "빙티",
    ),
)

package com.on.staccato.presentation.invitation.model

data class ReceivedInvitationUiModel(
    val invitationId: Long,
    val inviterId: Long,
    val inviterNickname: String,
    val inviterProfileImageUrl: String? = null,
    val categoryId: Long,
    val categoryTitle: String,
)

val dummyReceivedInvitationUiModels = listOf(
    ReceivedInvitationUiModel(
        invitationId = 0L,
        inviterId = 0L,
        inviterNickname = "호두",
        categoryId = 0L,
        categoryTitle = "여름 부산 여행",
    ),
    ReceivedInvitationUiModel(
        invitationId = 1L,
        inviterId = 1L,
        inviterNickname = "해나",
        categoryId = 1L,
        categoryTitle = "서울 존맛탱구리집 탐방",
    ),
    ReceivedInvitationUiModel(
        invitationId = 2L,
        inviterId = 2L,
        inviterNickname = "리니",
        categoryId = 2L,
        categoryTitle = "클라이밍하러 성수 꼬고~~~~~~",
    ),
    ReceivedInvitationUiModel(
        invitationId = 3L,
        inviterId = 3L,
        inviterNickname = "빙티",
        categoryId = 3L,
        categoryTitle = "클라이밍 도전기 클라이밍 도전기 클라이밍 도전기",
    ),
)

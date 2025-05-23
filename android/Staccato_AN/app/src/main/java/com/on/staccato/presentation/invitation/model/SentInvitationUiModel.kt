package com.on.staccato.presentation.invitation.model

data class SentInvitationUiModel(
    val invitationId: Long,
    val inviteeId: Long,
    val inviteeNickname: String,
    val inviteeProfileImageUrl: String? = null,
    val categoryId: Long,
    val categoryTitle: String,
)

private val dummySentInvitationUiModel =
    SentInvitationUiModel(
        invitationId = 0L,
        inviteeId = 0L,
        inviteeNickname = "초대 받은 사용자",
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
    )

val dummySentInvitationUiModels = listOf(
    SentInvitationUiModel(
        invitationId = 0L,
        inviteeId = 0L,
        inviteeNickname = "호두",
        categoryId = 0L,
        categoryTitle = "우아한테크코스",
    ),
    SentInvitationUiModel(
        invitationId = 2L,
        inviteeId = 100L,
        inviteeNickname = "사후르",
        categoryId = 2L,
        categoryTitle = "퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 3L,
        inviteeId = 1L,
        inviteeNickname = "리니"
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 4L,
        inviteeId = 3L,
        inviteeNickname = "해나",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 5L,
        inviteeId = 4L,
        inviteeNickname = "폭포",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 6L,
        inviteeId = 2L,
        inviteeNickname = "빙티",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 7L,
        inviteeId = 5L,
        inviteeNickname = "호티",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 8L,
        inviteeId = 6L,
        inviteeNickname = "카고",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 9L,
        inviteeId = 7L,
        inviteeNickname = "줄리",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 10L,
        inviteeId = 8L,
        inviteeNickname = "루엘",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 11L,
        inviteeId = 9L,
        inviteeNickname = "규니",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 12L,
        inviteeId = 10L,
        inviteeNickname = "케영",
    ),
    dummySentInvitationUiModel.copy(
        invitationId = 13L,
        inviteeId = 11L,
        inviteeNickname = "영미",
    ),
)

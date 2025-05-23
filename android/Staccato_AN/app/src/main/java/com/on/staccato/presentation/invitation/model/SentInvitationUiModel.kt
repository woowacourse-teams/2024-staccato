package com.on.staccato.presentation.invitation.model

data class SentInvitationUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val inviteeId: Long,
    val inviteeNickname: String,
    val inviteeProfileImageUrl: String? = null,
)

private val dummySentInvitationUiModel =
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 0L,
        inviteeNickname = "초대 받은 사용자",
    )

val dummySentInvitationUiModels = listOf(
    SentInvitationUiModel(
        categoryId = 0L,
        categoryTitle = "우아한테크코스",
        inviteeId = 0L,
        inviteeNickname = "호두",
    ),
    SentInvitationUiModel(
        categoryId = 2L,
        categoryTitle = "퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁",
        inviteeId = 100L,
        inviteeNickname = "사후르",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 1L,
        inviteeNickname = "리니"
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 3L,
        inviteeNickname = "해나",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 4L,
        inviteeNickname = "폭포",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 2L,
        inviteeNickname = "빙티",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 5L,
        inviteeNickname = "호티",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 6L,
        inviteeNickname = "카고",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 7L,
        inviteeNickname = "줄리",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 8L,
        inviteeNickname = "루엘",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 9L,
        inviteeNickname = "규니",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 10L,
        inviteeNickname = "케영",
    ),
    dummySentInvitationUiModel.copy(
        inviteeId = 11L,
        inviteeNickname = "영미",
    ),
)

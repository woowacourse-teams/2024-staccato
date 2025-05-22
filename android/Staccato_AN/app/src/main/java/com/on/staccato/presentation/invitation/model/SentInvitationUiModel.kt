package com.on.staccato.presentation.invitation.model

data class SentInvitationUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val inviteeId: Long,
    val inviteeNickname: String,
    val inviteeProfileImageUrl: String? = null,
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
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 1L,
        inviteeNickname = "리니",
    ),
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 3L,
        inviteeNickname = "해나",
    ),
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 4L,
        inviteeNickname = "폭포",
    ),
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 2L,
        inviteeNickname = "빙티",
    ),
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 5L,
        inviteeNickname = "호티",
    ),
    SentInvitationUiModel(
        categoryId = 1L,
        categoryTitle = "우당탕탕 스타카토 개발기",
        inviteeId = 6L,
        inviteeNickname = "카고",
    ),
)

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
        categoryTitle = "우당탕탕 스타카토 안드 개발기",
    )

val dummySentInvitationUiModels =
    listOf(
        SentInvitationUiModel(
            invitationId = 0L,
            inviteeId = 0L,
            inviteeNickname = "호두",
            categoryId = 0L,
            categoryTitle = "우아한테크코스",
        ),
        dummySentInvitationUiModel.copy(
            invitationId = 1L,
            inviteeId = 0L,
            inviteeNickname = "호두",
        ),
        dummySentInvitationUiModel.copy(
            invitationId = 2L,
            inviteeId = 1L,
            inviteeNickname = "빙티",
        ),
        dummySentInvitationUiModel.copy(
            invitationId = 3L,
            inviteeId = 2L,
            inviteeNickname = "해나",
        ),
    )

val dummySentInvitationLongTitle =
    SentInvitationUiModel(
        invitationId = 4L,
        inviteeId = 100L,
        inviteeNickname = "초대받은사람",
        categoryId = 2L,
        categoryTitle = "무지무지무지무지무지무지무지무지무지무지 기이이이이인 제목",
    )

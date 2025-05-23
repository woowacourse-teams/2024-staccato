package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel

fun ReceivedInvitation.toUiModel(): ReceivedInvitationUiModel =
    ReceivedInvitationUiModel(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        inviterId = inviter.memberId,
        inviterNickname = inviter.nickname,
        inviterProfileImageUrl = inviter.memberImage,
    )

fun SentInvitation.toUiModel(): SentInvitationUiModel =
    SentInvitationUiModel(
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        inviteeId = invitee.memberId,
        inviteeNickname = invitee.nickname,
        inviteeProfileImageUrl = invitee.memberImage,
    )

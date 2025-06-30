package com.on.staccato.presentation.mapper

import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel

fun ReceivedInvitation.toUiModel(): ReceivedInvitationUiModel =
    ReceivedInvitationUiModel(
        invitationId = invitationId,
        inviterId = inviter.memberId,
        inviterNickname = inviter.nickname,
        inviterProfileImageUrl = inviter.memberImage,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
    )

fun SentInvitation.toUiModel(): SentInvitationUiModel =
    SentInvitationUiModel(
        invitationId = invitationId,
        inviteeId = invitee.memberId,
        inviteeNickname = invitee.nickname,
        inviteeProfileImageUrl = invitee.memberImage,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
    )

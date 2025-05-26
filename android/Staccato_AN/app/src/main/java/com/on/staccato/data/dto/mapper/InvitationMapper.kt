package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.invitation.ReceivedInvitationDto
import com.on.staccato.data.dto.invitation.ReceivedInvitationsResponse
import com.on.staccato.data.dto.invitation.SentInvitationDto
import com.on.staccato.data.dto.invitation.SentInvitationsResponse
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation

fun ReceivedInvitationsResponse.toDomain(): List<ReceivedInvitation> =
    invitations.map { it.toDomain() }

fun ReceivedInvitationDto.toDomain(): ReceivedInvitation =
    ReceivedInvitation(
        invitationId = invitationId,
        inviter = Member(
            memberId = inviterId,
            nickname = inviterNickname,
            memberImage = inviterProfile,
        ),
        categoryId = categoryId,
        categoryTitle = categoryTitle,
    )

fun SentInvitationsResponse.toDomain(): List<SentInvitation> =
    invitations.map { it.toDomain() }

fun SentInvitationDto.toDomain(): SentInvitation =
    SentInvitation(
        invitationId = invitationId,
        invitee = Member(
            memberId = inviteeId,
            nickname = inviteeNickname,
            memberImage = inviteeProfileImageUrl,
        ),
        categoryId = categoryId,
        categoryTitle = categoryTitle,
    )

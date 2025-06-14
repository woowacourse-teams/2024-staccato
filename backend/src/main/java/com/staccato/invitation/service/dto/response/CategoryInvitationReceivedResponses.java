package com.staccato.invitation.service.dto.response;

import java.util.List;

import com.staccato.invitation.domain.CategoryInvitation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "받은 초대 목록 응답 형식입니다.")
public record CategoryInvitationReceivedResponses(List<CategoryInvitationReceivedResponse> invitations) {
    public static CategoryInvitationReceivedResponses from(List<CategoryInvitation> invitations) {
        return new CategoryInvitationReceivedResponses(invitations.stream()
                .map(CategoryInvitationReceivedResponse::new)
                .toList());
    }
}

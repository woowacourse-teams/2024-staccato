package com.staccato.invitation.service.dto.response;

import java.util.List;

import com.staccato.invitation.domain.CategoryInvitation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 초대 목록 응답 형식입니다.")
public record CategoryInvitationSentResponses(List<CategoryInvitationSentResponse> invitations) {
    public static CategoryInvitationSentResponses from(List<CategoryInvitation> invitations) {
        return new CategoryInvitationSentResponses(invitations.stream()
                .map(CategoryInvitationSentResponse::new)
                .toList());
    }
}

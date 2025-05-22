package com.staccato.invitation.service.dto.response;

import java.util.List;
import com.staccato.invitation.domain.CategoryInvitation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 초대 목록 응답 형식입니다.")
public record CategoryInvitationRequestedResponses(List<CategoryInvitationRequestedResponse> invitations) {
    public static CategoryInvitationRequestedResponses from(List<CategoryInvitation> invitations) {
        return new CategoryInvitationRequestedResponses(invitations.stream()
                .map(CategoryInvitationRequestedResponse::new)
                .toList());
    }
}

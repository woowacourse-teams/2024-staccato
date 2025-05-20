package com.staccato.invitation.service.dto.response;

import com.staccato.invitation.domain.CategoryInvitation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보낸 초대 목록에서 각 초대의 응답 형식입니다.")
public record CategoryInvitationRequestedResponse(
        InviteeResponse invitee,
        InvitedCategoryResponse category
) {
    public CategoryInvitationRequestedResponse(CategoryInvitation categoryInvitation) {
        this(
                new InviteeResponse(categoryInvitation.getInvitee()),
                new InvitedCategoryResponse(categoryInvitation.getCategory())
        );
    }
}

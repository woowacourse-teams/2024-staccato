package com.staccato.invitation.service.dto.response;

import java.util.List;
import com.staccato.invitation.domain.CategoryInvitation;

public record InvitationIdResponse(List<Long> invitationIds) {
    public static InvitationIdResponse from(List<CategoryInvitation> categoryInvitations) {
        return new InvitationIdResponse(categoryInvitations.stream()
                .map(CategoryInvitation::getId)
                .toList());
    }
}

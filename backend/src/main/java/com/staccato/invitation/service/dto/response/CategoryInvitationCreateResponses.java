package com.staccato.invitation.service.dto.response;

import java.util.List;
import com.staccato.invitation.domain.CategoryInvitation;

public record CategoryInvitationCreateResponses(List<Long> invitationIds) {
    public static CategoryInvitationCreateResponses from(List<CategoryInvitation> invitations) {
        return new CategoryInvitationCreateResponses(invitations.stream()
                .map(CategoryInvitation::getId)
                .toList());
    }
}

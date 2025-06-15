package com.staccato.invitation.service.dto.event;

import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record CategoryInvitationAcceptedEvent(Member invitee,
                                              Category category) {
}

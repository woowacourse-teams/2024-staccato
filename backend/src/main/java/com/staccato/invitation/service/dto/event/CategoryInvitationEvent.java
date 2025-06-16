package com.staccato.invitation.service.dto.event;

import java.util.List;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record CategoryInvitationEvent(Member inviter,
                                      List<Member> invitees,
                                      Category category) {
}

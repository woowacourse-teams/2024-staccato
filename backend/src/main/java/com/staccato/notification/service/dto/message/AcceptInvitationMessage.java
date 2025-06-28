package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record AcceptInvitationMessage(
        Member invitee,
        Category category
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "type", "ACCEPT_INVITATION",
                "categoryId", String.valueOf(category.getId())
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님이 참여했어요", invitee.getNickname().getNickname());
    }

    @Override
    public String getBody() {
        return String.format("%s에서 환영해주세요!", category.getTitle().getTitle());
    }
}

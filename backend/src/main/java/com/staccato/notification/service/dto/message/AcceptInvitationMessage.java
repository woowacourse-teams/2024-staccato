package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record AcceptInvitationMessage(
        String categoryId,
        String inviteeName,
        String categoryTitle
) implements PushMessage {
    public AcceptInvitationMessage(Member invitee, Category category) {
        this(String.valueOf(category.getId()), invitee.getNickname().getNickname(), category.getTitle().getTitle());
    }

    @Override
    public Notification toNotification() {
        return Notification.builder()
                .setTitle(getTitle())
                .setBody(getBody())
                .build();
    }

    @Override
    public Map<String, String> toData() {
        return Map.of(
                "title", getTitle(),
                "body", getBody(),
                "type", "ACCEPT_INVITATION",
                "categoryId", categoryId
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님이 참여했어요", inviteeName);
    }

    @Override
    public String getBody() {
        return String.format("%s에서 환영해주세요!", categoryTitle);
    }
}

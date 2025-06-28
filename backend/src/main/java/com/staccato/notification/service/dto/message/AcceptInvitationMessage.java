package com.staccato.notification.service.dto.message;

import java.util.Map;

public record AcceptInvitationMessage(
        String categoryId,
        String inviteeName,
        String categoryTitle
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "type", "ACCEPT_INVITATION",
                "categoryId", categoryId
        );
    }

    @Override
    public String getTitle() {
        return inviteeName + "님이 참여했어요";
    }

    @Override
    public String getBody() {
        return categoryTitle + "에서 환영해주세요!";
    }
}

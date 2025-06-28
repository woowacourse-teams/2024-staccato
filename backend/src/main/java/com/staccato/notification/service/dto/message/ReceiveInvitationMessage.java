package com.staccato.notification.service.dto.message;

import java.util.Map;

public record ReceiveInvitationMessage(
        String inviterName,
        String categoryTitle
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "type", "RECEIVE-INVITATION"
        );
    }

    @Override
    public String getTitle() {
        return inviterName + "님이 초대를 보냈어요";
    }

    @Override
    public String getBody() {
        return categoryTitle;
    }
}

package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record ReceiveInvitationMessage(
        Member inviter,
        Category category
) implements PushMessage {
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
                "type", "RECEIVE-INVITATION"
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님이 초대를 보냈어요", inviter.getNickname().getNickname());

    }

    @Override
    public String getBody() {
        return category.getTitle().getTitle();
    }
}

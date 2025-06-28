package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;

public record ReceiveInvitationMessage(
        String inviterName,
        String categoryTitle
) implements PushMessage {
    public ReceiveInvitationMessage(Member inviter, Category category){
        this(inviter.getNickname().getNickname(), category.getTitle().getTitle());
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
                "type", "RECEIVE_INVITATION"
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님이 초대를 보냈어요", inviterName);

    }

    @Override
    public String getBody() {
        return categoryTitle;
    }
}

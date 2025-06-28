package com.staccato.notification.service.dto.message;

import java.util.Map;
import com.google.firebase.messaging.Notification;
import com.staccato.category.domain.Category;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public record StaccatoCreatedMessage(
        String staccatoId,
        String staccatoCreatorName,
        String categoryTitle
) implements PushMessage {
    public StaccatoCreatedMessage(Member staccatoCreator, Staccato staccato, Category category) {
        this(String.valueOf(staccato.getId()), staccatoCreator.getNickname().getNickname(), category.getTitle()
                .getTitle());
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
                "type", "STACCATO_CREATED",
                "staccatoId", staccatoId
        );
    }

    @Override
    public String getTitle() {
        return "스타카토가 추가됐어요";
    }

    @Override
    public String getBody() {
        return String.format("%s님이 %s에 남긴 스타카토를 확인해보세요", staccatoCreatorName, categoryTitle);
    }
}

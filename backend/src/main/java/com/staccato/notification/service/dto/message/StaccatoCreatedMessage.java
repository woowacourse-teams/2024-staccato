package com.staccato.notification.service.dto.message;

import java.util.Map;

public record StaccatoCreatedMessage(
        String staccatoId,
        String staccatoCreatorName,
        String categoryTitle
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
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
        return staccatoCreatorName + "님이 " + categoryTitle + "에 남긴 스타카토를 확인해보세요";
    }
}

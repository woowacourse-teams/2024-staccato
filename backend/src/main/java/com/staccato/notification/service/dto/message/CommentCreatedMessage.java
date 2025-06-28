package com.staccato.notification.service.dto.message;

import java.util.Map;

public record CommentCreatedMessage(
        String staccatoId,
        String commentCreatorName,
        String commentContent
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "type", "COMMENT_CREATED",
                "staccatoId", staccatoId
        );
    }

    @Override
    public String getTitle() {
        return commentCreatorName + "님의 코멘트";
    }

    @Override
    public String getBody() {
        return commentContent;
    }
}

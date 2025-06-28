package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.google.firebase.messaging.Notification;
import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public record CommentCreatedMessage(
        String staccatoId,
        String commentCreatorName,
        String commentContent
) implements PushMessage {
    public CommentCreatedMessage(Member commentCreator, Staccato staccato, Comment comment){
        this(String.valueOf(staccato.getId()), commentCreator.getNickname().getNickname(), comment.getContent());
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
                "type", "COMMENT_CREATED",
                "staccatoId", staccatoId
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님의 코멘트", commentCreatorName);
    }

    @Override
    public String getBody() {
        return commentContent;
    }
}

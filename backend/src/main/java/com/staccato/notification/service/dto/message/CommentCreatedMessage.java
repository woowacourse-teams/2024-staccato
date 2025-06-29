package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.google.firebase.messaging.Notification;
import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public class CommentCreatedMessage extends PushMessage {
    private final String staccatoId;
    private final String commenterName;
    private final String commentContent;

    public CommentCreatedMessage(Member commentCreator, Staccato staccato, Comment comment){
        super(MessageType.COMMENT_CREATED);
        this.commenterName = commentCreator.getNickname().getNickname();
        this.commentContent = comment.getContent();
        this.staccatoId = String.valueOf(staccato.getId());
    }

    @Override
    protected String getTitle() {
        return String.format("%s님의 코멘트", commenterName);
    }

    @Override
    protected String getBody() {
        return commentContent;
    }

    @Override
    protected Map<String, String> getAdditionalData() {
        return Map.of("staccatoId", staccatoId);
    }
}

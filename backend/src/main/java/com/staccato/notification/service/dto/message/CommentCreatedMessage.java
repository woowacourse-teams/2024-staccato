package com.staccato.notification.service.dto.message;

import java.util.Map;

import com.staccato.comment.domain.Comment;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public record CommentCreatedMessage(
        Member commentCreator,
        Staccato staccato,
        Comment comment
) implements PushMessage {

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "type", "COMMENT_CREATED",
                "staccatoId", String.valueOf(staccato.getId())
        );
    }

    @Override
    public String getTitle() {
        return String.format("%s님의 코멘트", commentCreator.getNickname().getNickname());
    }

    @Override
    public String getBody() {
        return comment.getContent();
    }
}

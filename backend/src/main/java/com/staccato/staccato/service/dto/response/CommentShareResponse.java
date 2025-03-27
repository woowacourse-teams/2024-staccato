package com.staccato.staccato.service.dto.response;

import com.staccato.comment.domain.Comment;

public record CommentShareResponse(
        String nickname,
        String content,
        String memberImageUrl
) {
    public CommentShareResponse(Comment comment) {
        this(comment.getMember().getNickname().getNickname(),
                comment.getContent(),
                comment.getMember().getImageUrl());
    }
}

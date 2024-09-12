package com.staccato.fixture;

import com.staccato.comment.service.dto.request.CommentRequest;

public class CommentRequestFixture {
    public static CommentRequest create() {
        return new CommentRequest(1L, "content");
    }

    public static CommentRequest create(Long momentId) {
        return new CommentRequest(momentId, "content");
    }
}

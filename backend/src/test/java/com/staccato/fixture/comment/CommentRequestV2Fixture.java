package com.staccato.fixture.comment;

import com.staccato.comment.service.dto.request.CommentRequestV2;

public class CommentRequestV2Fixture {
    public static CommentRequestV2 create() {
        return new CommentRequestV2(1L, "content");
    }

    public static CommentRequestV2 create(Long staccatoId) {
        return new CommentRequestV2(staccatoId, "content");
    }
}

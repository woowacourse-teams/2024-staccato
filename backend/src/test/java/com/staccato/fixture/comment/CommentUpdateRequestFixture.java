package com.staccato.fixture.comment;

import com.staccato.comment.service.dto.request.CommentUpdateRequest;

public class CommentUpdateRequestFixture {
    public static CommentUpdateRequest create() {
        return new CommentUpdateRequest("updatedContent");
    }
}

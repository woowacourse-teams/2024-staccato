package com.staccato.fixture.comment;

import com.staccato.comment.service.dto.request.CommentUpdateRequest;

public class CommentUpdateRequestFixtures {

    public static CommentUpdateRequestBuilder defaultCommentUpdateRequest() {
        return new CommentUpdateRequestBuilder()
                .withContent("updatedContent");
    }

    public static class CommentUpdateRequestBuilder {
        String content;

        public CommentUpdateRequestBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CommentUpdateRequest build() {
            return new CommentUpdateRequest(content);
        }
    }
}

package com.staccato.fixture.comment;

import com.staccato.comment.service.dto.request.CommentRequest;

public class CommentRequestFixtures {

    public static CommentRequestBuilder defaultCommentRequest() {
        return new CommentRequestBuilder()
                .withStaccatoId(1L)
                .withContent("content");
    }

    public static class CommentRequestBuilder {
        Long staccatoId;
        String content;

        public CommentRequestBuilder withStaccatoId(Long staccatoId) {
            this.staccatoId = staccatoId;
            return this;
        }

        public CommentRequestBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CommentRequest build() {
            return new CommentRequest(staccatoId, content);
        }
    }
}

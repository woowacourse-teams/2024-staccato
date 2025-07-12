package com.staccato.comment.service.dto.response;

import java.util.List;

import com.staccato.comment.domain.Comment;

public record CommentResponsesV2(List<CommentResponseV2> comments) {
    public static CommentResponsesV2 from(List<Comment> comments) {
        return new CommentResponsesV2(
                comments.stream()
                        .map(CommentResponseV2::new)
                        .toList()
        );
    }

    public CommentResponses toCommentResponses() {
        return new CommentResponses(comments.stream()
                .map(CommentResponseV2::toCommentResponse)
                .toList());
    }
}

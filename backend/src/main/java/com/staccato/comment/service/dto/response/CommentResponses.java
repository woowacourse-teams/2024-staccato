package com.staccato.comment.service.dto.response;

import java.util.List;

import com.staccato.comment.domain.Comment;

public record CommentResponses(List<CommentResponse> comments) {
    public static CommentResponses from(List<Comment> comments) {
        return new CommentResponses(
                comments.stream()
                        .map(CommentResponse::new)
                        .toList()
        );
    }
}

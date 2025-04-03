package com.staccato.staccato.service.dto.response;

import com.staccato.comment.domain.Comment;
import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 공유했을 때 댓글 응답 형식입니다.")
public record CommentShareResponse(
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.COMMENT_CONTENT)
        String content,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String memberImageUrl
) {
    public CommentShareResponse(Comment comment) {
        this(comment.getMember().getNickname().getNickname(),
                comment.getContent(),
                comment.getMember().getImageUrl());
    }
}

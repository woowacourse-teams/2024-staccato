package com.staccato.comment.service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.comment.domain.Comment;
import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토에 대해 함께 한 친구와 나눈 대화 응답 형식입니다.")
public record CommentResponseV2(
        @Schema(example = SwaggerExamples.COMMENT_ID)
        Long commentId,
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImageUrl,
        @Schema(example = SwaggerExamples.COMMENT_CONTENT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String content,
        @Schema(example = SwaggerExamples.COMMENT_CREATED_AT)
        LocalDateTime createdAt,
        @Schema(example = SwaggerExamples.COMMENT_CREATED_AT)
        LocalDateTime updatedAt

) {
    public CommentResponseV2(Comment comment) {
        this(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname().getNickname(),
                comment.getMember().getImageUrl(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public CommentResponse toCommentResponse() {
        return new CommentResponse(
                commentId,
                memberId,
                nickname,
                memberImageUrl,
                content
        );
    }
}

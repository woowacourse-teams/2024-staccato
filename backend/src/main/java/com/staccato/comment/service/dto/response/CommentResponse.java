package com.staccato.comment.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.comment.domain.Comment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 순간 기록에 대해 함께 간 친구와 나눈 대화 응답 형식입니다.")
public record CommentResponse(
        @Schema(example = "1")
        Long commentId,
        @Schema(example = "1")
        Long memberId,
        @Schema(example = "카고")
        String nickname,
        @Schema(example = "https://example.com/images/kargo.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImageUrl,
        @Schema(example = "즐거운 추억")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String content
) {
    public CommentResponse(Comment comment) {
        this(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname().getNickname(),
                comment.getMember().getImageUrl(),
                comment.getContent()
        );
    }
}

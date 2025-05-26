package com.staccato.comment.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 수정 시 요청 형식입니다.")
public record CommentUpdateRequest(
        @Schema(example = SwaggerExamples.COMMENT_CONTENT)
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content
) {
}

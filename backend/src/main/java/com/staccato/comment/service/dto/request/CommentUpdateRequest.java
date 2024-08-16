package com.staccato.comment.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.staccato.exception.validation.ValidationSteps.FirstStep;
import com.staccato.exception.validation.ValidationSteps.SecondStep;

public record CommentUpdateRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.", groups = FirstStep.class)
        @Size(max = 500, message = "댓글은 공백 포함 500자 이하로 입력해주세요.", groups = SecondStep.class)
        String content
) {
}

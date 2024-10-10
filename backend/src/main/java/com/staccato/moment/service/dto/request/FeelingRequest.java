package com.staccato.moment.service.dto.request;

import jakarta.validation.constraints.NotNull;

import com.staccato.moment.domain.Feeling;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 기분 표현 요청")
public record FeelingRequest(
        @Schema(description = "기분 표현", example = "happy") @NotNull(message = "기분 값을 입력해주세요.") String feeling) {
    public Feeling toFeeling() {
        return Feeling.match(feeling);
    }
}

package com.staccato.memory.service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 조회 시 보여주는 스타카토의 정보에 대한 응답 형식입니다.")
public record StaccatoResponse(
        @Schema(example = "1")
        Long staccatoId,
        @Schema(example = "런던 아이")
        String staccatoTitle,
        @Schema(example = "https://example.com/memorys/london_eye.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String staccatoImageUrl,
        @Schema(example = "2024-07-27T11:58:20")
        LocalDateTime visitedAt
) {
    public StaccatoResponse(Moment moment, String momentImageUrl) {
        this(moment.getId(), moment.getTitle(), momentImageUrl, moment.getVisitedAt());
    }
}

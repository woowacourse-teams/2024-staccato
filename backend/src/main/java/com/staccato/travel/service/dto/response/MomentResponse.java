package com.staccato.travel.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.visit.domain.Visit;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 여행 상세 조회 시 보여주는 방문 기록의 정보에 대한 응답 형식입니다.")
public record MomentResponse(
        @Schema(example = "1")
        Long momentId,
        @Schema(example = "런던 아이")
        String placeName,
        @Schema(example = "https://example.com/travels/london_eye.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String momentImageUrl,
        @Schema(example = "2024-07-27")
        LocalDate visitedAt
) {
    public MomentResponse(Visit visit, String visitImage) {
        this(visit.getId(), visit.getPlaceName(), visitImage, visit.getVisitedAt().toLocalDate());
    }
}

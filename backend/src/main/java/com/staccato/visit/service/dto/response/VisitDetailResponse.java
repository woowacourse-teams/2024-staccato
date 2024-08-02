package com.staccato.visit.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 방문 기록을 조회했을 때 응답 형식입니다.")
public record VisitDetailResponse(
        @Schema(example = "1")
        long visitId,
        @Schema(example = "남산 서울타워")
        String placeName,
        @ArraySchema(
                arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        List<String> visitedImages,
        @Schema(example = "2021-11-08T11:58:20")
        LocalDate visitedAt,
        @Schema(example = "서울 용산구 남산공원길 105")
        String address,
        List<VisitLogResponse> visitLogs
) {
    public VisitDetailResponse(Visit visit) {
        this(
                visit.getId(),
                visit.getPlaceName(),
                visit.getVisitImages().stream().map(VisitImage::getImageUrl).toList(),
                visit.getVisitedAt(),
                visit.getSpot().getAddress(),
                visit.getVisitLogs().stream().map(VisitLogResponse::new).toList()
        );
    }
}

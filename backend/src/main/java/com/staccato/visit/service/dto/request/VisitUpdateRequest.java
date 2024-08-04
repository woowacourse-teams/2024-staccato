package com.staccato.visit.service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 방문 기록 수정 시 요청 형식입니다.")
public record VisitUpdateRequest(
        @Schema(example = "남산 서울타워")
        @NotNull
        String placeName,
        @ArraySchema(
                arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        List<String> visitImagesUrl) {
}

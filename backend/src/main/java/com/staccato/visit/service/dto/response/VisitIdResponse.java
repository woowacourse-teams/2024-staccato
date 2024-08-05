package com.staccato.visit.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방문 기록 생성 시 응답 형식입니다.")
public record VisitIdResponse(
        @Schema(example = "1")
        long visitId
) {
}

package com.staccato.moment.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순간 기록 생성 시 응답 형식입니다.")
public record MomentIdResponse(
        @Schema(example = "1")
        long momentId
) {
}

package com.staccato.staccato.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 생성 시 응답 형식입니다.")
public record StaccatoIdResponse(
        @Schema(example = "1")
        long staccatoId
) {
}

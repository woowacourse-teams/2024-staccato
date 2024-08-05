package com.staccato.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외에 대한 응답 형식입니다.")
public record ExceptionResponse(
        @Schema(example = "400 BAD_REQUEST")
        String status,
        @Schema(example = "요청하신 정보를 찾을 수 없어요.")
        String message
) {
}

package com.staccato.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외에 대한 응답 형식입니다.")
public record ExceptionResponse(
        String status,
        String message
) {
}

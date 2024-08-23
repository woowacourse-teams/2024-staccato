package com.staccato.auth.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 등록 시 발급되는 토큰에 대한 응답 형식입니다.")
public record LoginResponse(
        @Schema(example = "{tokenString}")
        String token
) {
}

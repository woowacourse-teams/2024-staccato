package com.staccato.memory.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억을 생성했을 때에 대한 응답 형식입니다.")
public record MemoryIdResponse(
        @Schema(example = "1")
        long memoryId
) {
}

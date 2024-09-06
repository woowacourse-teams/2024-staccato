package com.staccato.memory.service.dto.response;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 날짜를 포함하는 추억 목록 조회 시 각각의 추억에 대한 응답 형식입니다.")
public record MemoryNameResponse(
        @Schema(example = "1")
        Long memoryId,
        @Schema(example = "런던 추억")
        String memoryTitle
) {
    public MemoryNameResponse(Memory memory) {
        this(memory.getId(), memory.getTitle());
    }
}

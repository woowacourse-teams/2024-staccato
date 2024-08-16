package com.staccato.memory.service.dto.response;

import java.util.List;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억 목록 조회 시 반환 되는 응답 형식입니다.")
public record MemoryResponses(
        List<MemoryResponse> memories
) {
    public static MemoryResponses from(List<Memory> memories) {
        return new MemoryResponses(memories.stream()
                .map(MemoryResponse::new)
                .toList());
    }
}

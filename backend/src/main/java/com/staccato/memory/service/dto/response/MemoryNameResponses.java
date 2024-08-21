package com.staccato.memory.service.dto.response;

import java.util.List;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 날짜를 포함하는 추억 목록 조회 시 반환되는 응답 형식입니다.")
public record MemoryNameResponses(
        List<MemoryNameResponse> memories
) {
    public static MemoryNameResponses from(List<Memory> memories) {
        return new MemoryNameResponses(memories.stream()
                .map(MemoryNameResponse::new)
                .toList());
    }
}

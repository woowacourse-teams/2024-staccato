package com.staccato.memory.service.dto.response;

import java.util.List;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 반환 되는 응답 형식입니다.")
public record CategoryResponses(
        List<CategoryResponse> categories
) {
    public static CategoryResponses from(List<Memory> memories) {
        return new CategoryResponses(memories.stream()
                .map(CategoryResponse::new)
                .toList());
    }
}

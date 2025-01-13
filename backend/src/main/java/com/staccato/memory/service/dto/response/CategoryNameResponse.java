package com.staccato.memory.service.dto.response;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 날짜를 포함하는 카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryNameResponse(
        @Schema(example = "1")
        Long categoryId,
        @Schema(example = "런던 추억")
        String categoryTitle
) {
    public CategoryNameResponse(Memory memory) {
        this(memory.getId(), memory.getTitle());
    }
}

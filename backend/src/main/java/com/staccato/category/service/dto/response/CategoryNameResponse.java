package com.staccato.category.service.dto.response;

import com.staccato.category.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 날짜를 포함하는 카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryNameResponse(
        @Schema(example = "1")
        Long categoryId,
        @Schema(example = "런던 여행")
        String categoryTitle
) {
    public CategoryNameResponse(Category category) {
        this(category.getId(), category.getTitle());
    }
}

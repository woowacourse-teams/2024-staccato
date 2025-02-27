package com.staccato.category.service.dto.response;

import java.util.List;

import com.staccato.category.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 날짜를 포함하는 카테고리 목록 조회 시 반환되는 응답 형식입니다.")
public record CategoryNameResponses(
        List<CategoryNameResponse> categories
) {
    public static CategoryNameResponses from(List<Category> memories) {
        return new CategoryNameResponses(memories.stream()
                .map(CategoryNameResponse::new)
                .toList());
    }
}

package com.staccato.category.service.dto.response;

import com.staccato.category.domain.Category;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 반환 되는 응답 형식입니다.")
public record CategoryResponses(
        List<CategoryResponse> categories
) {
    public static CategoryResponses from(List<Category> categories) {
        return new CategoryResponses(categories.stream()
                .map(CategoryResponse::new)
                .toList());
    }
}

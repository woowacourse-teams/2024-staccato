package com.staccato.category.service.dto.response;

import java.util.List;
import com.staccato.category.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 반환 되는 응답 형식입니다.")
public record CategoryResponsesV3(
        List<CategoryResponseV3> categories
) {

    public CategoryResponses toCategoryResponses() {
        return new CategoryResponses(categories.stream()
                .map(CategoryResponseV3::toCategoryResponse)
                .toList());
    }

    public CategoryResponsesV2 toCategoryResponsesV2() {
        return new CategoryResponsesV2(categories.stream()
                .map(CategoryResponseV3::toCategoryResponseV2)
                .toList());
    }
}

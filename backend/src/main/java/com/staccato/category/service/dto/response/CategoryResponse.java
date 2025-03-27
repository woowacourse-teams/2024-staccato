package com.staccato.category.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryResponse(
        @Schema(example = "1")
        Long categoryId,
        @Schema(example = "https://example.com/categories/geumohrm.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryThumbnailUrl,
        @Schema(example = "런던 여행")
        String categoryTitle,
        @Schema(example = "2024-07-27")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt
) {
    public CategoryResponse(Category category) {
        this(
                category.getId(),
                category.getThumbnailUrl(),
                category.getTitle(),
                category.getTerm().getStartAt(),
                category.getTerm().getEndAt()
        );
    }
}

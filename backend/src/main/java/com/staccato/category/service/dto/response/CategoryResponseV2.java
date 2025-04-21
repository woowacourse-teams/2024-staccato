package com.staccato.category.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.Category;
import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryResponseV2(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        String categoryColor,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt
) {
    public CategoryResponseV2(Category category) {
        this(
                category.getId(),
                category.getThumbnailUrl(),
                category.getTitle(),
                category.getColor().getName(),
                category.getTerm().getStartAt(),
                category.getTerm().getEndAt()
        );
    }

    public CategoryResponse toCategoryResponse() {
        return new CategoryResponse(
                categoryId,
                categoryThumbnailUrl,
                categoryTitle,
                startAt,
                endAt
        );
    }
}

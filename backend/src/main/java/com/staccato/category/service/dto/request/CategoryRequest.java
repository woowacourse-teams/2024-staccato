package com.staccato.category.service.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.category.domain.Color;
import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리를 생성/수정하기 위한 요청 형식입니다.")
public record CategoryRequest(
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        @NotBlank(message = "카테고리 제목을 입력해주세요.")
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_DESCRIPTION)
        String description,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public CategoryRequest {
        if (Objects.nonNull(categoryTitle)) {
            categoryTitle = categoryTitle.trim();
        }
    }

    public CategoryUpdateRequest toCategoryUpdateRequest() {
        return new CategoryUpdateRequest(
                categoryThumbnailUrl,
                categoryTitle,
                description,
                Color.GRAY.getName(),
                startAt,
                endAt
        );
    }

    public CategoryCreateRequest toCategoryCreateRequest() {
        return new CategoryCreateRequest(
                categoryThumbnailUrl,
                categoryTitle,
                description,
                Color.GRAY.getName(),
                startAt,
                endAt,
                false
        );
    }
}

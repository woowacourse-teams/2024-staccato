package com.staccato.category.service.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.category.domain.Category;
import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리를 생성/수정하기 위한 요청 형식입니다.")
public record CategoryCreateRequest(
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        @NotBlank(message = "카테고리 제목을 입력해주세요.")
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_DESCRIPTION)
        String description,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        @NotBlank(message = "카테고리 색상을 선택해주세요.")
        String categoryColor,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt,
        @Schema(example = "false")
        @NotNull(message = "카테고리 공개 여부를 입력해주세요.")
        Boolean isShared) {
    public CategoryCreateRequest {
        if (Objects.nonNull(categoryTitle)) {
            categoryTitle = categoryTitle.trim();
        }
    }

    public Category toCategory() {
        return Category.builder()
                .thumbnailUrl(categoryThumbnailUrl)
                .title(categoryTitle)
                .description(description)
                .color(categoryColor)
                .startAt(startAt)
                .endAt(endAt)
                .isShared(isShared)
                .build();
    }
}

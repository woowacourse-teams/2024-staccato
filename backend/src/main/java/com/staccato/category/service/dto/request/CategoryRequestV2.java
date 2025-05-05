package com.staccato.category.service.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리를 생성/수정하기 위한 요청 형식입니다.")
public record CategoryRequestV2(
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String categoryThumbnailUrl,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        @NotBlank(message = "카테고리 제목을 입력해주세요.")
        @Size(max = 30, message = "제목은 공백 포함 30자 이하로 설정해주세요.")
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_DESCRIPTION)
        @Size(max = 500, message = "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        String description,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        @NotBlank(message = "카테고리 색상을 선택해주세요.")
        String categoryColor,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public CategoryRequestV2 {
        if (Objects.nonNull(categoryTitle)) {
            categoryTitle = categoryTitle.trim();
        }
    }

    public CategoryRequestV3 toCategoryRequestV3() {
        return new CategoryRequestV3(
                categoryThumbnailUrl,
                categoryTitle,
                description,
                categoryColor,
                startAt,
                endAt,
                false
        );
    }
}

package com.staccato.memory.service.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리를 생성/수정하기 위한 요청 형식입니다.")
public record CategoryRequest(
        @Schema(example = "http://example.com/london.png")
        String categoryThumbnailUrl,
        @Schema(example = "런던 추억")
        @NotBlank(message = "카테고리 제목을 입력해주세요.")
        @Size(max = 30, message = "제목은 공백 포함 30자 이하로 설정해주세요.")
        String categoryTitle,
        @Schema(example = "런던 시내 탐방")
        @Size(max = 500, message = "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        String description,
        @Schema(example = "2024-07-27")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public CategoryRequest {
        if (Objects.nonNull(categoryTitle)) {
            categoryTitle = categoryTitle.trim();
        }
    }

    public Memory toMemory() {
        return Memory.builder()
                .thumbnailUrl(categoryThumbnailUrl)
                .title(categoryTitle)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}

package com.staccato.memory.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 시 각각의 카테고리에 대한 응답 형식입니다.")
public record CategoryResponse(
        @Schema(example = "1")
        Long categoryId,
        @Schema(example = "https://example.com/memorys/geumohrm.jpg")
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
    public CategoryResponse(Memory memory) {
        this(
                memory.getId(),
                memory.getThumbnailUrl(),
                memory.getTitle(),
                memory.getTerm().getStartAt(),
                memory.getTerm().getEndAt()
        );
    }
}

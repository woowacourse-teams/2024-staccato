package com.staccato.memory.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억에 대한 응답 형식입니다.")
public record CategoryDetailResponse(
        @Schema(example = "1")
        Long categoryId,
        @Schema(example = "https://example.com/categorys/geumohrm.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String categoryThumbnailUrl,
        @Schema(example = "런던 추억")
        String categoryTitle,
        @Schema(example = "런던 시내 탐방")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description,
        @Schema(example = "2024-07-27")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt,
        List<MemberResponse> mates,
        List<StaccatoResponse> staccatos
) {
    public CategoryDetailResponse(Memory memory, List<StaccatoResponse> staccatoResponses) {
        this(
                memory.getId(),
                memory.getThumbnailUrl(),
                memory.getTitle(),
                memory.getDescription(),
                memory.getTerm().getStartAt(),
                memory.getTerm().getEndAt(),
                toMemberResponses(memory),
                staccatoResponses
        );
    }

    private static List<MemberResponse> toMemberResponses(Memory memory) {
        return memory.getMates().stream().map(MemberResponse::new).toList();
    }
}

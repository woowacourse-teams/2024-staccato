package com.staccato.travel.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.travel.domain.Travel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 여행 상세 정보에 대한 응답 형식입니다.")
public record MemoryDetailResponse(
        @Schema(example = "1")
        Long memoryId,
        @Schema(example = "https://example.com/travels/geumohrm.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memoryThumbnailUrl,
        @Schema(example = "런던 여행")
        String memoryTitle,
        @Schema(example = "런던 시내 탐방")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description,
        @Schema(example = "2024-07-27")
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        LocalDate endAt,
        List<MemberResponse> mates,
        List<MomentResponse> moments
) {
    public MemoryDetailResponse(Travel travel, List<MomentResponse> momentRespons) {
        this(
                travel.getId(),
                travel.getThumbnailUrl(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartAt(),
                travel.getEndAt(),
                toMemberResponses(travel),
                momentRespons
        );
    }

    private static List<MemberResponse> toMemberResponses(Travel travel) {
        return travel.getMates().stream().map(MemberResponse::new).toList();
    }
}

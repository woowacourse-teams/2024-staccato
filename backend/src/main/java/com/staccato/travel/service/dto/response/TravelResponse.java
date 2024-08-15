package com.staccato.travel.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.travel.domain.Travel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 상세 목록 조회 시 각각의 여행 상세에 대한 응답 형식입니다.")
public record TravelResponse(
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
        List<MemberResponse> mates
) {
    public TravelResponse(Travel travel) {
        this(
                travel.getId(),
                travel.getThumbnailUrl(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartAt(),
                travel.getEndAt(),
                createMemberResponses(travel.getMates())
        );
    }

    private static List<MemberResponse> createMemberResponses(List<Member> members) {
        return members.stream()
                .map(MemberResponse::new)
                .toList();
    }
}

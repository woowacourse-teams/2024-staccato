package com.staccato.memory.service.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberResponse;
import com.staccato.memory.domain.Memory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억 상세 목록 조회 시 각각의 추억 상세에 대한 응답 형식입니다.")
public record MemoryResponse(
        @Schema(example = "1")
        Long memoryId,
        @Schema(example = "https://example.com/memorys/geumohrm.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memoryThumbnailUrl,
        @Schema(example = "런던 추억")
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
    public MemoryResponse(Memory memory) {
        this(
                memory.getId(),
                memory.getThumbnailUrl(),
                memory.getTitle(),
                memory.getDescription(),
                memory.getStartAt(),
                memory.getEndAt(),
                createMemberResponses(memory.getMates())
        );
    }

    private static List<MemberResponse> createMemberResponses(List<Member> members) {
        return members.stream()
                .map(MemberResponse::new)
                .toList();
    }
}

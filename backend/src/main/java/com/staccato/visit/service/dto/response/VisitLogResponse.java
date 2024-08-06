package com.staccato.visit.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.visit.domain.VisitLog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 방문 기록에 대해 함께 간 친구와 나눈 대화 응답 형식입니다.")
public record VisitLogResponse(
        @Schema(example = "1")
        Long visitLogId,
        @Schema(example = "1")
        Long memberId,
        @Schema(example = "카고")
        String nickname,
        @Schema(example = "https://example.com/images/kargo.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImage,
        @Schema(example = "즐거운 여행")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String content
) {
    public VisitLogResponse(VisitLog visitLog) {
        this(
                visitLog.getId(),
                visitLog.getMember().getId(),
                visitLog.getMember().getNickname().getNickname(),
                visitLog.getMember().getImageUrl(),
                visitLog.getContent()
        );
    }
}

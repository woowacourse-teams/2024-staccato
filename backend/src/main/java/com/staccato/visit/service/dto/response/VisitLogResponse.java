package com.staccato.visit.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.visit.domain.VisitLog;

public record VisitLogResponse(
        Long visitLogId,
        Long memberId,
        String nickName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImage,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String context
) {
    public VisitLogResponse(VisitLog visitLog) {
        this(
                visitLog.getId(),
                visitLog.getMember().getId(),
                visitLog.getMember().getNickname(),
                visitLog.getMember().getImageUrl(),
                visitLog.getContent()
        );
    }
}

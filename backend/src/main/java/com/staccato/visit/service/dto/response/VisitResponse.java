package com.staccato.visit.service.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.visit.domain.Visit;

public record VisitResponse(
        Long visitId,
        String placeName,
        @JsonInclude(JsonInclude.Include.NON_NULL) String visitImage,
        LocalDate visitedAt
) {
    public VisitResponse(Visit visit, String visitImage) {
        this(visit.getId(), visit.getPin().getPlace(), visitImage, visit.getVisitedAt());
    }
}

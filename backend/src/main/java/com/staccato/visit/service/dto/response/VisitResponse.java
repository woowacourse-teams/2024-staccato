package com.staccato.visit.service.dto.response;

import java.time.LocalDate;

import com.staccato.visit.domain.Visit;

public record VisitResponse(
        Long visitId,
        String placeName,
        String visitImage,
        LocalDate visitedAt
) {
    public VisitResponse(Visit visit, String visitImage) {
        this(visit.getId(), visit.getPin().getPlace(), visitImage, visit.getVisitedAt());
    }
}

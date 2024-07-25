package com.staccato.visit.service.dto.response;

import java.time.LocalDate;

public record VisitResponse(
        Long visitId,
        String placeName,
        String visitedImages,
        LocalDate visitedAt
) {
}

package com.staccato.visit.fixture;

import java.time.LocalDate;
import java.util.List;

import com.staccato.visit.service.dto.response.VisitDetailResponse;

public class VisitDetailResponseFixture {
    public static VisitDetailResponse create(long visitId, LocalDate visitedAt) {
        return new VisitDetailResponse(visitId,
                "placeName",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "address",
                List.of());
    }
}

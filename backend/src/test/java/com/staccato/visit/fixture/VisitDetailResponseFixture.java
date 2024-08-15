package com.staccato.visit.fixture;

import java.time.LocalDate;
import java.util.List;

import com.staccato.visit.service.dto.response.MomentDetailResponse;

public class VisitDetailResponseFixture {
    public static MomentDetailResponse create(long visitId, LocalDate visitedAt) {
        return new MomentDetailResponse(visitId,
                "placeName",
                List.of("https://example1.com.jpg"),
                visitedAt,
                "address",
                List.of());
    }
}

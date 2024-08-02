package com.staccato.visit.fixture;

import java.time.LocalDate;
import java.util.List;

import com.staccato.visit.service.dto.request.VisitRequest;

public class VisitRequestFixture {
    private static VisitRequest create(LocalDate visitedAt, long travelId) {
        return new VisitRequest("placeName", "address", VisitFixture.latitude, VisitFixture.longitude, List.of("https://example1.com.jpg"), visitedAt, travelId);
    }
}

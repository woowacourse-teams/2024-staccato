package com.staccato.visit.fixture;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.staccato.travel.domain.Travel;
import com.staccato.visit.domain.Visit;

public class VisitFixture {
    private static final BigDecimal latitude = new BigDecimal("37.7749");
    private static final BigDecimal longitude = new BigDecimal("-122.4194");

    public static Visit create(Travel travel, LocalDate visitedAt) {
        return Visit.builder()
                .visitedAt(visitedAt)
                .placeName("placeName")
                .latitude(latitude)
                .longitude(longitude)
                .address("address")
                .travel(travel)
                .build();
    }
}

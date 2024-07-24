package com.staccato.travel.service.dto.response;

import java.util.List;

import com.staccato.travel.domain.Travel;

public record TravelResponses(
        List<TravelResponse> travels
) {
    public static TravelResponses from(List<Travel> travels) {
        return new TravelResponses(travels.stream()
                .map(TravelResponse::new)
                .toList());
    }
}

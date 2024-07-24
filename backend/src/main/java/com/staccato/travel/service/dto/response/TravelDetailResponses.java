package com.staccato.travel.service.dto.response;

import java.util.List;

import com.staccato.travel.domain.Travel;

public record TravelDetailResponses(
        List<TravelDetailResponse> travels
) {
    public static TravelDetailResponses from(List<Travel> travels) {
        return new TravelDetailResponses(travels.stream()
                .map(TravelDetailResponse::new)
                .toList());
    }
}

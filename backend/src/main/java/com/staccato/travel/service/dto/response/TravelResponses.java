package com.staccato.travel.service.dto.response;

import java.util.List;

import com.staccato.travel.domain.Travel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 상세 목록 조회 시 반환 되는 응답 형식입니다.")
public record TravelResponses(
        List<TravelResponse> travels
) {
    public static TravelResponses from(List<Travel> travels) {
        return new TravelResponses(travels.stream()
                .map(TravelResponse::new)
                .toList());
    }
}

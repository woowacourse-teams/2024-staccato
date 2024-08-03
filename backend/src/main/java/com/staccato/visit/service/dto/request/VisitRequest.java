package com.staccato.visit.service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.travel.domain.Travel;
import com.staccato.visit.domain.Visit;

public record VisitRequest(
        @NotNull(message = "방문한 장소 이름을 입력해주세요.")
        String placeName,
        @NotNull(message = "방문한 장소의 주소를 입력해주세요.")
        String address,
        @NotNull(message = "방문한 장소의 위도를 입력해주세요.")
        BigDecimal latitude,
        @NotNull(message = "방문한 장소의 경도를 입력해주세요.")
        BigDecimal longitude,
        List<String> visitImagesUrl,
        @NotNull(message = "방문 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate visitedAt,
        @NotNull(message = "여행 상세를 선택해주세요.")
        @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.")
        Long travelId
) {
    public Visit toVisit(Travel travel) {
        return Visit.builder()
                .visitedAt(visitedAt)
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .travel(travel)
                .build();
    }
}

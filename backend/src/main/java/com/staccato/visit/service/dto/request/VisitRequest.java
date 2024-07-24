package com.staccato.visit.service.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.pin.domain.Pin;
import com.staccato.travel.domain.Travel;
import com.staccato.visit.domain.Visit;

public record VisitRequest(
        @NotNull(message = "핀을 선택해주세요.")
        Long pinId,
        List<String> visitedImages,
        @NotNull(message = "방문 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate visitedAt,
        @NotNull(message = "여행 상세를 선택해주세요.")
        Long travelId) {
    public Visit toVisit(Pin pin, Travel travel) {
        return Visit.builder()
                .visitedAt(visitedAt)
                .pin(pin)
                .travel(travel)
                .build();
    }
}

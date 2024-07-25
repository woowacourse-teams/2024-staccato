package com.staccato.travel.service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.travel.domain.Travel;

public record TravelRequest(
        String travelThumbnail,
        @NotNull(message = "여행 제목을 입력해주세요.")
        @Size(max = 30, message = "제목의 최대 허용 글자수는 공백 포함 30자입니다.")
        String travelTitle,
        @Size(max = 500, message = "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        String description,
        @NotNull(message = "여행 시작 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @NotNull(message = "여행 끝 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public Travel toTravel() {
        return Travel.builder()
                .thumbnailUrl(travelThumbnail)
                .title(travelTitle)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}

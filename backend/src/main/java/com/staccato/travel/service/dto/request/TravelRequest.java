package com.staccato.travel.service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.travel.domain.Travel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 상세를 생성/수정하기 위한 요청 형식입니다.")
public record TravelRequest(
        @Schema(example = "http://example.com/london.png")
        String travelThumbnailUrl,
        @Schema(example = "런던 여행")
        @NotBlank(message = "여행 제목을 입력해주세요.")
        @Size(min = 1, max = 30, message = "제목은 공백 포함 1자 이상 30자 이하로 설정해주세요.")
        String travelTitle,
        @Schema(example = "런던 시내 탐방")
        @Size(max = 500, message = "내용의 최대 허용 글자수는 공백 포함 500자입니다.")
        String description,
        @Schema(example = "2024-07-27")
        @NotNull(message = "여행 시작 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startAt,
        @Schema(example = "2024-07-29")
        @NotNull(message = "여행 끝 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endAt) {
    public Travel toTravel() {
        return Travel.builder()
                .thumbnailUrl(travelThumbnailUrl)
                .title(travelTitle)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}

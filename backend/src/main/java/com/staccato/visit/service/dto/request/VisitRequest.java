package com.staccato.visit.service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.staccato.travel.domain.Travel;
import com.staccato.visit.domain.Visit;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방문 기록 생성 시 요청 형식입니다. 단, 멀티파트로 보내는 사진 파일은 여기에 포함되지 않습니다.")
public record VisitRequest(
        @Schema(example = "런던 박물관")
        @NotBlank(message = "방문한 장소의 이름을 입력해주세요.")
        @Size(min = 1, max = 30, message = "방문한 장소의 이름은 공백 포함 1자 이상 30자 이하로 설정해주세요.")
        String placeName,
        @Schema(example = "Great Russell St, London WC1B 3DG")
        @NotNull(message = "방문한 장소의 주소를 입력해주세요.")
        String address,
        @Schema(example = "51.51978412729915")
        @NotNull(message = "방문한 장소의 위도를 입력해주세요.")
        BigDecimal latitude,
        @Schema(example = "-0.12712788587027796")
        @NotNull(message = "방문한 장소의 경도를 입력해주세요.")
        BigDecimal longitude,
        @Schema(example = "2024-07-27")
        @NotNull(message = "방문 날짜를 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime visitedAt,
        @Schema(example = "1")
        @NotNull(message = "여행 상세를 선택해주세요.")
        @Min(value = 1L, message = "여행 식별자는 양수로 이루어져야 합니다.")
        long memoryId
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

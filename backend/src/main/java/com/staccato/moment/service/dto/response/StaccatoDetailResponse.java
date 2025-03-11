package com.staccato.moment.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.moment.domain.Staccato;
import com.staccato.moment.domain.StaccatoImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 조회했을 때 응답 형식입니다.")
public record StaccatoDetailResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "1")
        long categoryId,
        @Schema(example = "2024 서울 투어")
        String categoryTitle,
        @Schema(example = "2024-06-30")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = "2024-07-04")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt,
        @Schema(example = "즐거웠던 남산에서의 기억")
        String staccatoTitle,
        @ArraySchema(arraySchema = @Schema(example = "[\"https://example.com/images/namsan_tower.jpg\", \"https://example.com/images/namsan_tower2.jpg\"]"))
        List<String> staccatoImageUrls,
        @Schema(example = "2021-11-08T11:58:20")
        LocalDateTime visitedAt,
        @Schema(example = "happy")
        String feeling,
        @Schema(example = "남산서울타워")
        String placeName,
        @Schema(example = "서울 용산구 남산공원길 105")
        String address,
        @Schema(example = "51.51978412729915")
        BigDecimal latitude,
        @Schema(example = "-0.12712788587027796")
        BigDecimal longitude
) {
    public StaccatoDetailResponse(Staccato staccato) {
        this(
                staccato.getId(),
                staccato.getCategory().getId(),
                staccato.getCategory().getTitle(),
                staccato.getCategory().getTerm().getStartAt(),
                staccato.getCategory().getTerm().getEndAt(),
                staccato.getTitle(),
                staccato.getStaccatoImages().getImages().stream().map(StaccatoImage::getImageUrl).toList(),
                staccato.getVisitedAt(),
                staccato.getFeeling().getValue(),
                staccato.getSpot().getPlaceName(),
                staccato.getSpot().getAddress(),
                staccato.getSpot().getLatitude(),
                staccato.getSpot().getLongitude()
        );
    }
}

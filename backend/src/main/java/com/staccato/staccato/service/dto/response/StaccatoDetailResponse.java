package com.staccato.staccato.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.domain.StaccatoImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토를 조회했을 때 응답 형식입니다.")
public record StaccatoDetailResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        Long staccatoId,
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        Long categoryId,
        @Schema(example = SwaggerExamples.CATEGORY_TITLE)
        String categoryTitle,
        @Schema(example = SwaggerExamples.CATEGORY_START_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate startAt,
        @Schema(example = SwaggerExamples.CATEGORY_END_AT)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDate endAt,
        @Schema(example = SwaggerExamples.STACCATO_TITLE)
        String staccatoTitle,
        @ArraySchema(arraySchema = @Schema(example = SwaggerExamples.IMAGE_URLS))
        List<String> staccatoImageUrls,
        @Schema(example = SwaggerExamples.STACCATO_VISITED_AT)
        LocalDateTime visitedAt,
        @Schema(example = SwaggerExamples.FEELING)
        String feeling,
        @Schema(example = SwaggerExamples.STACCATO_PLACE_NAME)
        String placeName,
        @Schema(example = SwaggerExamples.STACCATO_ADDRESS)
        String address,
        @Schema(example = SwaggerExamples.STACCATO_LATITUDE)
        BigDecimal latitude,
        @Schema(example = SwaggerExamples.STACCATO_LONGITUDE)
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

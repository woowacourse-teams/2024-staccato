package com.staccato.moment.service.dto.response;

import java.math.BigDecimal;

import com.staccato.moment.domain.Staccato;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 목록 중 하나의 스타카토에 해당하는 응답입니다.")
public record StaccatoLocationResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "51.51978412729915")
        BigDecimal latitude,
        @Schema(example = "-0.12712788587027796")
        BigDecimal longitude) {

    public StaccatoLocationResponse(Staccato staccato) {
        this(staccato.getId(), staccato.getSpot().getLatitude(), staccato.getSpot().getLongitude());
    }
}

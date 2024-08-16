package com.staccato.moment.service.dto.response;

import java.math.BigDecimal;

import com.staccato.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순간 목록 중 하나의 순간에 해당하는 응답입니다.")
public record MomentResponse(
        @Schema(example = "1")
        long momentId,
        @Schema(example = "51.51978412729915")
        BigDecimal latitude,
        @Schema(example = "-0.12712788587027796")
        BigDecimal longitude) {

    public MomentResponse(Moment moment) {
        this(moment.getId(), moment.getSpot().getLatitude(), moment.getSpot().getLongitude());
    }
}

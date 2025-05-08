package com.staccato.staccato.service.dto.response;

import java.math.BigDecimal;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 목록 중 하나의 스타카토에 해당하는 응답입니다.")
public record StaccatoLocationResponseV2(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        Long staccatoId,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        String staccatoColor,
        @Schema(example = SwaggerExamples.STACCATO_LATITUDE)
        BigDecimal latitude,
        @Schema(example = SwaggerExamples.STACCATO_LONGITUDE)
        BigDecimal longitude
) {

    public StaccatoLocationResponseV2(Staccato staccato) {
        this(staccato.getId(), staccato.getColor().getName(), staccato.getSpot().getLatitude(), staccato.getSpot()
                .getLongitude());
    }

    public StaccatoLocationResponse toStaccatoLocationResponse() {
        return new StaccatoLocationResponse(
                staccatoId,
                latitude,
                longitude
        );
    }
}

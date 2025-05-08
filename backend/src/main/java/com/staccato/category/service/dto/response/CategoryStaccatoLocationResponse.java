package com.staccato.category.service.dto.response;

import java.math.BigDecimal;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 속한 스타카토 응답 형식입니다.")
public record CategoryStaccatoLocationResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        Long staccatoId,
        @Schema(example = SwaggerExamples.CATEGORY_COLOR)
        String staccatoColor,
        @Schema(example = SwaggerExamples.STACCATO_LATITUDE)
        BigDecimal latitude,
        @Schema(example = SwaggerExamples.STACCATO_LONGITUDE)
        BigDecimal longitude
) {

    public CategoryStaccatoLocationResponse(Staccato staccato) {
        this(staccato.getId(), staccato.getColor().getName(), staccato.getSpot().getLatitude(), staccato.getSpot().getLongitude());
    }
}

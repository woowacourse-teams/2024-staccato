package com.staccato.staccato.service.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 조회 시 위경도 query String 형식입니다.")
public record StaccatoLocationRangeRequest(
        @Schema(description = "북동쪽 위도", example = SwaggerExamples.STACCATO_LATITUDE)
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
        BigDecimal neLat,
        @Schema(description = "북동쪽 경도", example = SwaggerExamples.STACCATO_LONGITUDE)
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
        BigDecimal neLng,
        @Schema(description = "남서쪽 위도", example = SwaggerExamples.STACCATO_LATITUDE)
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
        BigDecimal swLat,
        @Schema(description = "남서쪽 경도", example = SwaggerExamples.STACCATO_LONGITUDE)
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
        BigDecimal swLng
) {

    public static StaccatoLocationRangeRequest empty() {
        return new StaccatoLocationRangeRequest(null, null, null, null);
    }
}

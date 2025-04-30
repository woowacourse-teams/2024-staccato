package com.staccato.staccato.service.dto.request;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 조회 시 위경도 query String 형식입니다.")
public record ReadStaccatoRequest(
        BigDecimal neLat,
        BigDecimal neLng,
        BigDecimal swLat,
        BigDecimal swLng
) {

}

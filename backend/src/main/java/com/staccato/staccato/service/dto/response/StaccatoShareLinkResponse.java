package com.staccato.staccato.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

public record StaccatoShareLinkResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "https://staccato.kr/share/sample-token")
        String shareLink,
        @JsonIgnore
        String token
) {
}

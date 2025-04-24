package com.staccato.staccato.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

public record StaccatoShareLinkResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        long staccatoId,
        @Schema(example = SwaggerExamples.SHARE_LINK)
        String shareLink,
        @JsonIgnore
        String token
) {
}

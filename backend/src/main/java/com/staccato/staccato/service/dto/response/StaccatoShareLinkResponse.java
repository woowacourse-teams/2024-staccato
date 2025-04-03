package com.staccato.staccato.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

public record StaccatoShareLinkResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        long staccatoId,
        @Schema(example = SwaggerExamples.SHARE_LINK)
        String shareLink
) {
        private static final String SHARE_LINK_PREFIX = "https://staccato.kr/share/";

        public String getToken() {
                return shareLink.substring(SHARE_LINK_PREFIX.length());
        }
}

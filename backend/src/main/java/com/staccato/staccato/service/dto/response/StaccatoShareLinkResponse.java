package com.staccato.staccato.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StaccatoShareLinkResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "https://staccato.kr/share/sample-token")
        String shareLink
) {
        private static final String SHARE_LINK_PREFIX = "https://staccato.kr/share/";

        public String getToken() {
                return shareLink.substring(SHARE_LINK_PREFIX.length());
        }
}

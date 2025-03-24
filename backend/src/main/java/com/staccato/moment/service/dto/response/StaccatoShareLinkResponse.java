package com.staccato.moment.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record StaccatoShareLinkResponse(
        @Schema(example = "1")
        long staccatoId,
        @Schema(example = "https://staccato.kr/staccato?token=sample-token")
        String shareLink
) {
        private static final String SHARE_LINK_PREFIX = "https://staccato.kr/share?token=";

        public String getToken() {
                return shareLink.substring(SHARE_LINK_PREFIX.length());
        }
}

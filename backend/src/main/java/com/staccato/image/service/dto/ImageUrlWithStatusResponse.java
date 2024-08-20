package com.staccato.image.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드를 했을 때 응답 형식입니다.")
public record ImageUrlWithStatusResponse(
        @Schema(example = "200 OK")
        String status,
        @Schema(example = "https://d1234abcdefg.cloudfront.net/staccato/image/abcdefg.jpg")
        String imageUrl
) {
}

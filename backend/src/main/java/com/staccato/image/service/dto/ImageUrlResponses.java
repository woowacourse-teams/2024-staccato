package com.staccato.image.service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드를 했을 때 응답 형식입니다.")
public record ImageUrlResponses(List<ImageUrlWithStatusResponse> images) {
}

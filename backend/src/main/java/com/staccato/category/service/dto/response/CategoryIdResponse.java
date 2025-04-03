package com.staccato.category.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리를 생성했을 때에 대한 응답 형식입니다.")
public record CategoryIdResponse(
        @Schema(example = SwaggerExamples.CATEGORY_ID)
        long categoryId
) {
}

package com.staccato.category.service.dto.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.staccato.domain.Staccato;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 조회 시 보여주는 스타카토의 정보에 대한 응답 형식입니다.")
public record StaccatoResponse(
        @Schema(example = SwaggerExamples.STACCATO_ID)
        Long staccatoId,
        @Schema(example = SwaggerExamples.STACCATO_TITLE)
        String staccatoTitle,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String staccatoImageUrl,
        @Schema(example = SwaggerExamples.STACCATO_VISITED_AT)
        LocalDateTime visitedAt
) {
    public StaccatoResponse(Staccato staccato) {
        this(staccato.getId(), staccato.getTitle().getTitle(), staccato.thumbnailUrl(), staccato.getVisitedAt());
    }
}

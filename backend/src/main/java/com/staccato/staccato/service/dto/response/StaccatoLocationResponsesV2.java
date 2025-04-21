package com.staccato.staccato.service.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 목록에 해당하는 응답입니다.")
public record StaccatoLocationResponsesV2(List<StaccatoLocationResponseV2> staccatoLocationResponses) {
    public StaccatoLocationResponses toStaccatoLocationResponses() {
        return new StaccatoLocationResponses(staccatoLocationResponses.stream()
                .map(StaccatoLocationResponseV2::toStaccatoLocationResponse)
                .toList());
    }
}

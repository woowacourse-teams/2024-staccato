package com.staccato.moment.service.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스타카토 목록에 해당하는 응답입니다.")
public record StaccatoLocationResponses(List<StaccatoLocationResponse> staccatoLocationResponses) {
}

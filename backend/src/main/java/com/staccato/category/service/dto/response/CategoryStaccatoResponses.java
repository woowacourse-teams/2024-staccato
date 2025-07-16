package com.staccato.category.service.dto.response;

import java.util.List;
import com.staccato.category.service.StaccatoCursor;
import com.staccato.staccato.domain.Staccato;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 속한 스타카토 목록에 해당하는 응답입니다.")
public record CategoryStaccatoResponses(
        List<StaccatoResponse> staccatos,
        String nextCursor
) {

    public static CategoryStaccatoResponses of(List<Staccato> staccatos, StaccatoCursor nextCursor) {
        List<StaccatoResponse> staccatoResponses = staccatos.stream().map(StaccatoResponse::new).toList();
        return new CategoryStaccatoResponses(staccatoResponses, nextCursor.encode());
    }
}

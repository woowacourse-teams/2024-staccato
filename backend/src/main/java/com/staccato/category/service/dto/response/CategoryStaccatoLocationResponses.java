package com.staccato.category.service.dto.response;

import java.util.List;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponseV2;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponses;
import com.staccato.staccato.service.dto.response.StaccatoLocationResponsesV2;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 속한 스타카토 목록에 해당하는 응답입니다.")
public record CategoryStaccatoLocationResponses(List<CategoryStaccatoLocationResponse> categoryStaccatoLocationResponses) {

    public static CategoryStaccatoLocationResponses of(List<Staccato> staccatos){
        List<CategoryStaccatoLocationResponse> categoryStaccatoLocationResponses = staccatos.stream()
                .map(CategoryStaccatoLocationResponse::new)
                .toList();
        return new CategoryStaccatoLocationResponses(categoryStaccatoLocationResponses);
    }
}

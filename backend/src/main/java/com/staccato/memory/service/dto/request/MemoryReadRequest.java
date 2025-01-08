package com.staccato.memory.service.dto.request;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억 목록 조회시 정렬과 필터링 조건을 위한 요청 형식입니다.")
public record MemoryReadRequest(
        @Schema(description = "정렬 기준 (UPDATED, NEWEST, OLDEST)", example = "NEWEST")
        String sort,
        @Schema(description = "기간 필터 사용 여부", example = "true")
        String term
) {
    private static final String TERM = "term";
    private static final String ACTIVE = "true";

    public List<String> getFilters() {
        List<String> filters = new ArrayList<>();
        if (isActive(term)) {
            filters.add(TERM.toLowerCase());
        }
        return filters;
    }

    private boolean isActive(String term) {
        return term.equalsIgnoreCase(ACTIVE);
    }
}

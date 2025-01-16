package com.staccato.memory.service.dto.request;

import java.util.Arrays;
import java.util.List;
import com.staccato.memory.service.MemoryFilter;
import com.staccato.memory.service.MemorySort;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추억 목록 조회시 정렬과 필터링 조건을 위한 요청 형식입니다.")
public record MemoryReadRequest(
        @Schema(description = "사용할 필터링을 구분자(,)로 구분하여 나열 (TERM / 대소문자 구분 X)", example = "TERM")
        String filters,
        @Schema(description = "정렬 기준 (UPDATED, NEWEST, OLDEST / 대소문자 구분 X)", example = "NEWEST")
        String sort
) {
    private static final String DELIMITER = ",";

    public List<MemoryFilter> getFilters() {
        List<String> filters = Arrays.stream(this.filters.split(DELIMITER))
                .map(String::trim)
                .toList();
        return MemoryFilter.findAllByName(filters);
    }

    public MemorySort getSort() {
        return MemorySort.findByName(sort);
    }
}

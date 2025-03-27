package com.staccato.category.service.dto.request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.staccato.category.service.CategoryFilter;
import com.staccato.category.service.CategorySort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회시 정렬과 필터링 조건을 위한 요청 형식입니다.")
public record CategoryReadRequest(
        @Schema(description = "사용할 필터링을 구분자(,)로 구분하여 나열 (WITH_TERM, WITHOUT_TERM / 대소문자 구분 X)", example = "WITH_TERM")
        String filters,
        @Schema(description = "정렬 기준 (UPDATED, NEWEST, OLDEST / 대소문자 구분 X)", example = "NEWEST")
        String sort
) {
    private static final String DELIMITER = ",";

    public List<CategoryFilter> getFilters() {
        List<String> filters = parseFilters().stream()
                .map(String::trim)
                .toList();
        return CategoryFilter.findAllByName(filters);
    }

    private List<String> parseFilters() {
        if (Objects.isNull(filters)) {
            return List.of();
        }
        return Arrays.stream(filters.split(DELIMITER)).toList();
    }

    public CategorySort getSort() {
        return CategorySort.findByName(sort);
    }
}

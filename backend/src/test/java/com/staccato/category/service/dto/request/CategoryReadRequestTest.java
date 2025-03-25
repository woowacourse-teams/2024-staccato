package com.staccato.category.service.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.staccato.category.service.CategoryFilter;
import com.staccato.category.service.CategorySort;

class CategoryReadRequestTest {
    @DisplayName("필터가 주어졌을 때 올바른 필터 목록을 반환한다")
    @Test
    void getFiltersWithValidFilters() {
        // given
        CategoryReadRequest request = new CategoryReadRequest("TERM, term", "NEWEST");

        // when
        List<CategoryFilter> filters = request.getFilters();

        // then
        assertThat(filters).hasSize(1).containsOnly(CategoryFilter.TERM);
    }

    @DisplayName("필터가 주어지지 않았을 때 빈 필터 목록을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void getFiltersWithNullOrEmptyFilters(String filters) {
        // given
        CategoryReadRequest request = new CategoryReadRequest(filters, "NEWEST");

        // when
        List<CategoryFilter> result = request.getFilters();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("정렬이 주어지지 않았을 때 기본 정렬 기준을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void getSortWithNullOrEmptyFilters(String sort) {
        // given
        CategoryReadRequest request = new CategoryReadRequest(null, sort);

        // when
        CategorySort result = request.getSort();

        // then
        assertThat(result).isEqualTo(CategorySort.UPDATED);
    }
}

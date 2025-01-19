package com.staccato.memory.service.dto.request;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import com.staccato.memory.service.MemoryFilter;
import com.staccato.memory.service.MemorySort;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryReadRequestTest {
    @DisplayName("필터가 주어졌을 때 올바른 MemoryFilter 리스트를 반환한다")
    @Test
    void getFiltersWithValidFilters() {
        // given
        MemoryReadRequest request = new MemoryReadRequest("TERM, term", "NEWEST");

        // when
        List<MemoryFilter> filters = request.getFilters();

        // then
        assertThat(filters).hasSize(1).containsOnly(MemoryFilter.TERM);
    }

    @DisplayName("필터가 주어지지 않았을 때 빈 리스트를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void getFiltersWithNullOrEmptyFilters(String filters) {
        // given
        MemoryReadRequest request = new MemoryReadRequest(filters, "NEWEST");

        // when
        List<MemoryFilter> result = request.getFilters();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("정렬이 주어지지 않았을 때 기본 정렬 기준을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void getSortWithNullOrEmptyFilters(String sort) {
        // given
        MemoryReadRequest request = new MemoryReadRequest(null, sort);

        // when
        MemorySort result = request.getSort();

        // then
        assertThat(result).isEqualTo(MemorySort.UPDATED);
    }
}

package com.staccato.fixture.memory;

import com.staccato.memory.service.dto.response.CategoryResponse;
import com.staccato.memory.service.dto.response.CategoryResponses;
import java.util.Arrays;
import java.util.List;
import com.staccato.memory.domain.Memory;

public class CategoryResponsesFixture {
    public static CategoryResponses create(Memory... memories) {
        return new CategoryResponses(convertToCategoryResponses(Arrays.stream(memories).toList()));
    }

    private static List<CategoryResponse> convertToCategoryResponses(List<Memory> memory) {
        return memory.stream().map(CategoryResponse::new).toList();
    }
}

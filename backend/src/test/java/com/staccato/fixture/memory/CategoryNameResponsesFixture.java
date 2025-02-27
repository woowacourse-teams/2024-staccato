package com.staccato.fixture.memory;

import com.staccato.memory.service.dto.response.CategoryNameResponses;
import java.util.Arrays;
import java.util.List;
import com.staccato.memory.domain.Memory;

public class CategoryNameResponsesFixture {
    public static CategoryNameResponses create(Memory... memory) {
        List<Memory> memories = Arrays.stream(memory).toList();
        return CategoryNameResponses.from(memories);
    }
}

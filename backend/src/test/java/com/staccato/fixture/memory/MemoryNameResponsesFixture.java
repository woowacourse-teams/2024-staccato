package com.staccato.fixture.memory;

import java.util.Arrays;
import java.util.List;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.service.dto.response.MemoryNameResponses;

public class MemoryNameResponsesFixture {
    public static MemoryNameResponses create(Memory... memory) {
        List<Memory> memories = Arrays.stream(memory).toList();
        return MemoryNameResponses.from(memories);
    }
}

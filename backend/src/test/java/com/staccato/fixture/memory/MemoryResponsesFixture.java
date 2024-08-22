package com.staccato.fixture.memory;

import java.util.Arrays;
import java.util.List;

import com.staccato.memory.domain.Memory;
import com.staccato.memory.service.dto.response.MemoryResponse;
import com.staccato.memory.service.dto.response.MemoryResponses;

public class MemoryResponsesFixture {
    public static MemoryResponses create(Memory... memories) {
        return new MemoryResponses(convertToMemoryResponses(Arrays.stream(memories).toList()));
    }

    private static List<MemoryResponse> convertToMemoryResponses(List<Memory> memory) {
        return memory.stream().map(MemoryResponse::new).toList();
    }
}

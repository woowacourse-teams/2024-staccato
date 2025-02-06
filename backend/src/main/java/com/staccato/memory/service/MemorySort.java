package com.staccato.memory.service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import com.staccato.memory.domain.Memory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemorySort {
    UPDATED("UPDATED", memoryList ->
            memoryList.stream()
                    .sorted(Comparator.comparing(Memory::getUpdatedAt).reversed())
                    .toList()
    ),
    NEWEST("NEWEST", memoryList ->
            memoryList.stream()
                    .sorted(Comparator.comparing(Memory::getCreatedAt).reversed())
                    .toList()
    ),
    OLDEST("OLDEST", memoryList ->
            memoryList.stream()
                    .sorted(Comparator.comparing(Memory::getCreatedAt))
                    .toList()
    );

    private final String name;
    private final Function<List<Memory>, List<Memory>> operation;

    public List<Memory> apply(List<Memory> memories) {
        return operation.apply(memories);
    }

    public static MemorySort findByName(String name) {
        return Stream.of(values())
                .filter(sort -> sort.isSame(name))
                .findFirst()
                .orElse(UPDATED);
    }

    private boolean isSame(String name) {
        return this.name.equalsIgnoreCase(name);
    }
}

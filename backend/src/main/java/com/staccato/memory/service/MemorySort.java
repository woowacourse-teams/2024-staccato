package com.staccato.memory.service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import com.staccato.memory.domain.Memory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemorySort {
    RECENTLY_UPDATED("UPDATED", memoryList ->
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

    public static List<Memory> apply(String sortValue, List<Memory> memories) {
        return Stream.of(MemorySort.values())
                .filter(sort -> sort.isSame(sortValue))
                .findFirst()
                .map(sort -> sort.operation.apply(memories))
                .orElse(RECENTLY_UPDATED.operation.apply(memories));
    }

    private boolean isSame(String sortValue) {
        return this.name.equalsIgnoreCase(sortValue);
    }
}

package com.staccato.memory.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.staccato.memory.domain.Memory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemoryFilter {
    TERM("term", memoryList ->
            memoryList.stream()
                    .filter(Memory::hasTerm)
                    .collect(Collectors.toList())
    );

    private final String name;
    private final Function<List<Memory>, List<Memory>> operation;

    public static List<Memory> apply(List<MemoryFilter> filters, List<Memory> memories) {
        for (MemoryFilter filter : filters) {
            memories = filter.operation.apply(memories);
        }
        return memories;
    }

    public static List<MemoryFilter> findAllByName(List<String> filters) {
        return filters.stream()
                .map(name -> MemoryFilter.findByName(name.trim()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private static Optional<MemoryFilter> findByName(String name) {
        return Stream.of(values())
                .filter(value -> value.isSame(name))
                .findFirst();
    }

    private boolean isSame(String name) {
        return this.name.equalsIgnoreCase(name);
    }
}

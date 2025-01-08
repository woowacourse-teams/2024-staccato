package com.staccato.memory.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.staccato.memory.domain.Memory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemoryFilter {
    FILTER_WITH_TERM("term", memoryList ->
            memoryList.stream()
                    .filter(Memory::hasTerm)
                    .collect(Collectors.toList())
    );

    private final String name;
    private final Function<List<Memory>, List<Memory>> operation;

    public static List<Memory> apply(List<String> filters, List<Memory> memories) {
        List<MemoryFilter> applicableFilters = Stream.of(MemoryFilter.values())
                .filter(filter -> filters.contains(filter.name))
                .toList();

        List<Memory> filteredMemories = memories;
        for (MemoryFilter filter : applicableFilters) {
            filteredMemories = filter.operation.apply(filteredMemories);
        }
        return filteredMemories;
    }
}

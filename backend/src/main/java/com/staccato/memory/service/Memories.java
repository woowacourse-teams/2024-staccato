package com.staccato.memory.service;

import java.util.List;
import java.util.stream.Collectors;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Memories {
    private static final List<MemoryFilter> DEFAULT_MEMORY_FILTER = List.of();
    private static final MemorySort DEFAULT_MEMORY_SORT = MemorySort.UPDATED;
    
    private final List<Memory> memories;

    public static Memories from(List<MemoryMember> memoryMembers) {
        return new Memories(memoryMembers.stream().map(MemoryMember::getMemory).collect(Collectors.toList()));
    }

    public List<Memory> operate(List<MemoryFilter> filters, MemorySort sort) {
        List<Memory> filteredMemories = MemoryFilter.apply(filters, memories);
        return sort.apply(filteredMemories);
    }

    public List<Memory> operate() {
        return operate(DEFAULT_MEMORY_FILTER, DEFAULT_MEMORY_SORT);
    }
}

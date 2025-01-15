package com.staccato.memory.service;

import java.util.List;
import java.util.stream.Collectors;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Memories {
    private final List<Memory> memories;

    public static Memories from(List<MemoryMember> memoryMembers) {
        return new Memories(memoryMembers.stream().map(MemoryMember::getMemory).collect(Collectors.toList()));
    }

    public List<Memory> operate(List<String> filters, String sort) {
        List<Memory> filteredMemories = MemoryFilter.apply(filters, memories);
        return MemorySort.apply(sort, filteredMemories);
    }

    public List<Memory> operate() {
        return operate(List.of(), null);
    }
}

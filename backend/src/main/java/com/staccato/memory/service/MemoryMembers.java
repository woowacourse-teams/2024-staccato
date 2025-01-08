package com.staccato.memory.service;

import java.util.List;
import java.util.stream.Collectors;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryMembers {
    private final List<MemoryMember> memoryMembers;

    public List<Memory> operate(List<String> filters, String sort) {
        List<Memory> memories = getMemories();
        List<Memory> filteredMemories = MemoryFilter.apply(filters, memories);
        return MemorySort.apply(sort, filteredMemories);
    }

    public List<Memory> operate() {
        return operate(List.of(), null);
    }

    private List<Memory> getMemories() {
        return memoryMembers.stream()
                .map(MemoryMember::getMemory)
                .collect(Collectors.toList());
    }
}

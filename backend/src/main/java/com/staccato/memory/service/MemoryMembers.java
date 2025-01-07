package com.staccato.memory.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryMembers {
    private final List<MemoryMember> memoryMembers;

    public List<Memory> descendMemoryByUpdatedAt() {
        List<Memory> memories = getMemories();
        memories.sort(Comparator.comparing(Memory::getUpdatedAt).reversed());
        return memories;
    }

    private List<Memory> getMemories() {
        return memoryMembers.stream()
                .map(MemoryMember::getMemory)
                .collect(Collectors.toList());
    }
}

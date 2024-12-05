package com.staccato.memory.service;

import java.util.Comparator;
import java.util.List;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.domain.MemoryMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryMembers {
    private final List<MemoryMember> memoryMembers;

    public void descendByCreatedDate() {
        memoryMembers.sort(Comparator.comparing(MemoryMember::getCreatedAt).reversed());
    }

    public List<Memory> getMemories() {
        return memoryMembers.stream()
                .map(MemoryMember::getMemory)
                .toList();
    }
}

package com.staccato.memory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.memory.domain.Memory;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    boolean existsByTitle(String title);
}

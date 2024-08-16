package com.staccato.moment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.memory.domain.Memory;
import com.staccato.moment.domain.Moment;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    List<Moment> findAllByMemoryIdOrderByVisitedAt(long memoryId);

    boolean existsByMemory(Memory memory);

    void deleteAllByMemoryId(long memoryId);
}

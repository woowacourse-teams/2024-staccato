package com.staccato.moment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

public interface MomentRepository extends JpaRepository<Moment, Long> {

    @Query("SELECT m FROM Moment m LEFT JOIN FETCH m.momentImages.images WHERE m.memory.id = :memoryId ORDER BY m.visitedAt")
    List<Moment> findAllByMemoryIdOrderByVisitedAt(@Param("memoryId") long memoryId);

    void deleteAllByMemoryId(long memoryId);

    List<Moment> findAllByMemory_MemoryMembers_Member(Member member);

    List<Moment> findAllByMemoryId(long memoryId);
}

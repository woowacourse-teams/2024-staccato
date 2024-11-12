package com.staccato.moment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    @Query("SELECT m FROM Moment m WHERE m.memory.id = :memoryId order by m.visitedAt desc, m.createdAt desc")
    List<Moment> findAllByMemoryIdOrdered(long memoryId);

    List<Moment> findAllByMemory_MemoryMembers_Member(Member member);

    List<Moment> findAllByMemoryId(long memoryId);

    @Modifying
    @Query("DELETE FROM Moment m WHERE m.memory.id = :memoryId")
    void deleteAllByMemoryIdInBatch(@Param("memoryId") Long memoryId);
}

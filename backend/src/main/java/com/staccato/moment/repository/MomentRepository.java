package com.staccato.moment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    List<Moment> findAllByMemoryIdOrderByVisitedAt(long memoryId);

    void deleteAllByMemoryId(long memoryId);

    List<Moment> findAllByMemory_MemoryMembers_Member(Member member);

    List<Moment> findAllByMemoryId(long memoryId);
}

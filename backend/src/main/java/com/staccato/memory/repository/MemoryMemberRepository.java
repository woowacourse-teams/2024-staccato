package com.staccato.memory.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.member.domain.Member;
import com.staccato.memory.domain.MemoryMember;

public interface MemoryMemberRepository extends JpaRepository<MemoryMember, Long> {

    @Query("SELECT mm FROM MemoryMember mm JOIN FETCH mm.memory WHERE mm.member.id = :memberId")
    List<MemoryMember> findAllByMemberId(long memberId);

    @Query("""
            SELECT mm FROM MemoryMember mm JOIN FETCH mm.memory WHERE mm.member.id = :memberId
            AND ((mm.memory.term.startAt is null AND mm.memory.term.endAt is null)
            or (:date BETWEEN mm.memory.term.startAt AND mm.memory.term.endAt))
            """)
    List<MemoryMember> findAllByMemberIdAndIncludingDate(@Param("memberId") long memberId, @Param("date") LocalDate date);

    boolean existsByMemberAndMemoryTitle(Member member, String title);

    @Modifying
    @Query("DELETE FROM MemoryMember mm WHERE mm.memory.id = :memoryId")
    void deleteAllByMemoryIdInBatch(@Param("memoryId") Long memoryId);
}

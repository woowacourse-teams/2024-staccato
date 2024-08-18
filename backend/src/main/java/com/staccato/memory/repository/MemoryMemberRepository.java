package com.staccato.memory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.memory.domain.MemoryMember;

public interface MemoryMemberRepository extends JpaRepository<MemoryMember, Long> {
    List<MemoryMember> findAllByMemberIdOrderByMemoryCreatedAtDesc(long memberId);

    @Query("SELECT mm FROM MemoryMember mm WHERE mm.member.id = :memberId AND YEAR(mm.memory.term.startAt) = :year ORDER BY mm.memory.createdAt DESC")
    List<MemoryMember> findAllByMemberIdAndYearOrderByCreatedAtDesc(@Param("memberId") long memberId, @Param("year") int year);
}

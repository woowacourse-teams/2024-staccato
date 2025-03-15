package com.staccato.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(Nickname nickname);

    Optional<Member> findByCode(String code);

    @Query("""
        SELECT m FROM Member m
        JOIN MemoryMember mm ON mm.member.id = m.id
        WHERE mm.memory.id = :memoryId
    """)
    Optional<Member> findByMemoryId(@Param("memoryId") Long memoryId);
}

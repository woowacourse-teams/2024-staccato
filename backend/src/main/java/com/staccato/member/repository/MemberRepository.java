package com.staccato.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(Nickname nickname);

    Optional<Member> findByCode(String code);
}

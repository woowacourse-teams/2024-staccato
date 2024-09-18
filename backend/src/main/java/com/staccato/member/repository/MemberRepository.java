package com.staccato.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(Nickname nickname);

    Member findByNickname(Nickname nickname);
}

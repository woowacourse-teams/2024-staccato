package com.staccato.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndIsDeletedIsFalse(long memberId);

    boolean existsByNickname(Nickname nickname);
}

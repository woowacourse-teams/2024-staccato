package com.staccato.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

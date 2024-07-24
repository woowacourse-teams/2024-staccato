package com.staccato.travel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.travel.domain.TravelMember;

public interface TravelMemberRepository extends JpaRepository<TravelMember, Long> {
    List<TravelMember> findAllByMemberId(long memberId);
}

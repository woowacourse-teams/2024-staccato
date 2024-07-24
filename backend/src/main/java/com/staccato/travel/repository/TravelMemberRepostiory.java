package com.staccato.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.travel.domain.TravelMember;

public interface TravelMemberRepostiory extends JpaRepository<TravelMember, Long> {
}

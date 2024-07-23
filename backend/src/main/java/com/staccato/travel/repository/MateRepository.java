package com.staccato.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.travel.domain.Mate;

public interface MateRepository extends JpaRepository<Mate, Long> {
}

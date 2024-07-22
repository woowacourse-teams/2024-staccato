package com.staccato.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
}

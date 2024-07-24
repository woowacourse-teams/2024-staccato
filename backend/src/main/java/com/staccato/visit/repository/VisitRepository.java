package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelId(Long travelId);
}

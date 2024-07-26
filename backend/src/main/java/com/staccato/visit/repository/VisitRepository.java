package com.staccato.visit.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelIdOrderByVisitedAt(long travelId);

    long countByPinIdAndVisitedAtBefore(Long pinId, LocalDate visitedAt);

    boolean existsByTravelId(Long travelId);

    void deleteAllByTravelId(long travelId);
}

package com.staccato.visit.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelIdAndIsDeletedIsFalseOrderByVisitedAt(long travelId);

    long countByPinIdAndIsDeletedIsFalseAndVisitedAtBefore(Long pinId, LocalDate visitedAt);

    boolean existsByTravelIdAndIsDeletedIsFalse(Long travelId);

    void deleteAllByTravelIdAndIsDeletedIsFalse(long travelId);
}

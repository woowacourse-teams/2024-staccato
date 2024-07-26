package com.staccato.visit.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelIdAndIsDeletedIsFalseOrderByVisitedAt(long travelId);

    long countByPinIdAndIsDeletedIsFalseAndVisitedAtBefore(Long pinId, LocalDate visitedAt);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Visit v WHERE v.travel.id = :travelId AND v.isDeleted = false")
    boolean existsByTravelIdAndIsDeleteIsFalse(@Param("travelId") Long travelId);

    void deleteAllByTravelIdAndIsDeletedIsFalse(long travelId);
}

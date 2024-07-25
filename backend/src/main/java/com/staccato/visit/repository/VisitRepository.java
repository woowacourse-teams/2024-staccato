package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelIdAndIsDeletedIsFalse(@Param("travelId") long travelId);
}

package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.travel.domain.Travel;
import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelIdOrderByVisitedAt(long travelId);

    boolean existsByTravel(Travel travel);

    void deleteAllByTravelId(long travelId);
}

package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByTravelId(Long travelId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Visit vi SET vi.isDeleted = true WHERE vi.travel.id = :travelId")
    void deleteByTravelId(@Param("travelId") Long travelId);
}

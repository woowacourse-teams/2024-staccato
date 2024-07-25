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

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Visit v WHERE v.travel.id = :travelId")
    boolean existsByTravelId(@Param("travelId") Long travelId);
}

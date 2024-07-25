package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.VisitImage;

public interface VisitImageRepository extends JpaRepository<VisitImage, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE VisitImage vi SET vi.isDeleted = true WHERE vi.visit.id = :visitId")
    void deleteByVisitId(@Param("visitId") Long visitId);

    List<VisitImage> findAllByVisitId(Long visitId);
}

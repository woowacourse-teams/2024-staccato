package com.staccato.visit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.VisitImage;

public interface VisitImageRepository extends JpaRepository<VisitImage, Long> {
    @Modifying(clearAutomatically = true)
    void deleteByVisitId(@Param("visitId") Long visitId);

    Optional<VisitImage> findFirstByVisitId(long visitId);

    List<VisitImage> findAllByVisitId(long visitId);
}

package com.staccato.visit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.VisitImage;

public interface VisitImageRepository extends JpaRepository<VisitImage, Long> {
    Optional<VisitImage> findFirstByVisitId(long visitId);

    List<VisitImage> findAllByVisitId(long visitId);
}

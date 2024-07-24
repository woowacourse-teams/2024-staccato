package com.staccato.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.VisitImage;

public interface VisitImageRepository extends JpaRepository<VisitImage, Long> {
}

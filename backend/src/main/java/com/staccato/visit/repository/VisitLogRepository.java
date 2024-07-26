package com.staccato.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staccato.visit.domain.VisitLog;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
}

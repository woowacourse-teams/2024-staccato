package com.staccato.visit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.VisitLog;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    @Modifying(clearAutomatically = true)
    void deleteByVisitId(@Param("visitId") Long visitId);

    List<VisitLog> findAllByVisitId(long visitId);
}

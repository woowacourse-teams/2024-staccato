package com.staccato.visit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.visit.domain.VisitLog;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    @Modifying
    @Query("UPDATE VisitLog v SET v.isDeleted = true WHERE v.visit.id = :visitId")
    void deleteByVisitId(@Param("visitId") Long visitId);
}

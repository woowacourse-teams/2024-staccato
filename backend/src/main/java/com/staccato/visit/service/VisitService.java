package com.staccato.visit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitLogRepository visitLogRepository;

    public void deleteById(Long visitId) {
        visitLogRepository.deleteByVisitId(visitId);
        visitRepository.deleteById(visitId);
    }
}

package com.staccato.visit.service;

import com.staccato.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;

    public void deleteById(Long visitId) {
        visitRepository.deleteById(visitId);
    }
}

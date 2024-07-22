package com.staccato.visit.service;

import org.springframework.stereotype.Service;

import com.staccato.visit.repository.VisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
}

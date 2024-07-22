package com.staccato.visit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.exception.InvalidIdException;
import com.staccato.visit.service.VisitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private static final int MIN_ID = 1;

    private final VisitService visitService;

    @DeleteMapping("{visitId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long visitId) {
        if (visitId < MIN_ID) {
            throw new InvalidIdException("방문 기록 식별자는 양수로 이루어져야 합니다.");
        }
        visitService.deleteById(visitId);
        return ResponseEntity.noContent().build();
    }
}

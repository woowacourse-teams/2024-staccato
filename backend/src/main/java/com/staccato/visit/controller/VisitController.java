package com.staccato.visit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staccato.visit.service.VisitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @DeleteMapping("{visitId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long visitId) {
        visitService.deleteById(visitId);
        return ResponseEntity.noContent().build();
    }
}
